[CmdletBinding()]
param(
    [string]$Profile = "local",
    [string]$JavafxPlatform = "",
    [string]$ShortcutName = "Gestore Agenti.lnk",
    [switch]$Force
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$startScript = Join-Path $repoRoot "scripts/start-desktop.ps1"

if (-not (Test-Path -LiteralPath $startScript)) {
    throw "Script di avvio non trovato: $startScript"
}

$desktopPath = [Environment]::GetFolderPath("Desktop")
if (-not $desktopPath) {
    throw "Percorso Desktop non trovato per l'utente corrente."
}

if (-not $ShortcutName.EndsWith('.lnk')) {
    $ShortcutName = "$ShortcutName.lnk"
}

$shortcutPath = Join-Path $desktopPath $ShortcutName
if ((Test-Path -LiteralPath $shortcutPath) -and -not $Force) {
    throw "Il collegamento esiste già: $shortcutPath. Usa -Force per sovrascriverlo."
}

$targetPath = "powershell.exe"
$arguments = @(
    "-NoProfile",
    "-ExecutionPolicy", "Bypass",
    "-File", "`"$startScript`"",
    "-Profile", $Profile
)

if ($JavafxPlatform) {
    $arguments += @("-JavafxPlatform", $JavafxPlatform)
}

$shell = New-Object -ComObject WScript.Shell
$shortcut = $shell.CreateShortcut($shortcutPath)
$shortcut.TargetPath = $targetPath
$shortcut.Arguments = ($arguments -join ' ')
$shortcut.WorkingDirectory = $repoRoot
$shortcut.IconLocation = "$Env:SystemRoot\\System32\\WindowsPowerShell\\v1.0\\powershell.exe,0"
$shortcut.Save()

Write-Host "Collegamento creato: $shortcutPath" -ForegroundColor Green
