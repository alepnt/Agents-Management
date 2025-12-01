[CmdletBinding()]
param(
    [string]$Profile = "local",
    [string]$JavafxPlatform = ""
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$envFile = Join-Path $repoRoot ".env"

function Import-EnvFile {
    param([string]$Path)
    if (-not (Test-Path -LiteralPath $Path)) {
        return
    }

    Get-Content -LiteralPath $Path | ForEach-Object {
        if (-not $_ -or $_.Trim().StartsWith('#')) { return }
        $parts = $_ -split '=', 2
        if ($parts.Length -eq 2 -and $parts[0].Trim()) {
            $name = $parts[0].Trim()
            $value = $parts[1]
            if (-not [string]::IsNullOrWhiteSpace($value)) {
                $Env:$name = $value
            }
        }
    }
}

function Ensure-Command {
    param([string]$Command)
    if (-not (Get-Command $Command -ErrorAction SilentlyContinue)) {
        throw "Comando richiesto non trovato: $Command"
    }
}

function Wait-ForHealth {
    param(
        [string]$Url,
        [int]$TimeoutSeconds = 60
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        try {
            $response = Invoke-WebRequest -UseBasicParsing -Uri $Url -TimeoutSec 5
            if ($response.StatusCode -eq 200) { return }
        } catch {
            if ($_.Exception.Response.StatusCode.value__ -eq 404) {
                return
            }
        }

        if ($script:serverProcess -and $script:serverProcess.HasExited) {
            throw "Il server si è arrestato con codice $($script:serverProcess.ExitCode)."
        }

        Start-Sleep -Seconds 3
    }

    throw "Timeout in attesa dell'endpoint $Url"
}

Ensure-Command "mvn"
Ensure-Command "java"

if (Test-Path -LiteralPath $envFile) {
    Write-Host "Caricamento variabili da $envFile" -ForegroundColor Cyan
    Import-EnvFile -Path $envFile
}

if (-not $Env:DB_URL) { $Env:DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=gestoreagenti;encrypt=true;trustServerCertificate=true" }
if (-not $Env:DB_USERNAME) { $Env:DB_USERNAME = "sa" }
if (-not $Env:DB_PASSWORD) { $Env:DB_PASSWORD = "ChangeMe!" }
if (-not $Env:DB_MAX_POOL_SIZE) { $Env:DB_MAX_POOL_SIZE = "10" }
if (-not $Env:DB_MIN_IDLE) { $Env:DB_MIN_IDLE = "5" }
if (-not $Env:SPRING_PROFILES_ACTIVE) { $Env:SPRING_PROFILES_ACTIVE = $Profile }

if (-not $Env:MSAL_CLIENT_ID) {
    Write-Warning "MSAL_CLIENT_ID non impostato: assicurati di configurarlo prima di lanciare il client."
}

Write-Host "Compilazione del server..." -ForegroundColor Cyan
& mvn -pl server -am -ntp clean package
if ($LASTEXITCODE -ne 0) {
    throw "La build Maven del server è fallita."
}

$serverJar = Get-ChildItem -Path (Join-Path $repoRoot "server/target") -Filter "gestore-agenti-server-*.jar" | Sort-Object LastWriteTime -Descending | Select-Object -First 1
if (-not $serverJar) {
    throw "JAR del server non trovato in server/target."
}

Write-Host "Avvio del server ($($serverJar.Name))..." -ForegroundColor Cyan
$serverArgs = @("-jar", $serverJar.FullName)
$script:serverProcess = Start-Process -FilePath "java" -ArgumentList $serverArgs -WorkingDirectory $repoRoot -PassThru -NoNewWindow

try {
    Write-Host "Attesa disponibilità server su http://localhost:8080..." -ForegroundColor Cyan
    Wait-ForHealth -Url "http://localhost:8080/actuator/health"

    $clientArgs = @("-pl", "client", "-am", "-ntp", "javafx:run")
    if ($JavafxPlatform) {
        $clientArgs += "-Djavafx.platform=$JavafxPlatform"
    }

    Write-Host "Avvio del client JavaFX..." -ForegroundColor Cyan
    & mvn @clientArgs
    if ($LASTEXITCODE -ne 0) {
        throw "L'esecuzione del client JavaFX è terminata con errori."
    }
}
finally {
    if ($script:serverProcess -and -not $script:serverProcess.HasExited) {
        Write-Host "Arresto del server..." -ForegroundColor Cyan
        Stop-Process -Id $script:serverProcess.Id -Force
    }
}
