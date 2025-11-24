// Pacchetto che contiene le classi di sicurezza dell'applicazione server.
package com.example.server.security;

// Importazione della classe MSAL per creare applicazioni client confidenziali.
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
// Importazione dell'interfaccia che rappresenta le credenziali del client.
import com.microsoft.aad.msal4j.IClientCredential;
// Importazione della factory che costruisce le credenziali a partire dai segreti.
import com.microsoft.aad.msal4j.ClientCredentialFactory;
// Importazione per leggere valori di configurazione dal file properties di Spring.
import org.springframework.beans.factory.annotation.Value;
// Importazione per registrare la classe come componente Spring.
import org.springframework.stereotype.Component;

// Importazione della classe per gestire URL non validi.
import java.net.MalformedURLException;
// Importazione per gestire le eccezioni di esecuzione asincrona.
import java.util.concurrent.ExecutionException;

/**
 * Factory centralizzata per l'integrazione con Microsoft Identity Platform.
 */
// Annotazione che rende la classe gestita dal contesto di Spring come componente.
@Component
public class MsalClientProvider {

    // Identificativo dell'applicazione registrata in Azure AD.
    private final String clientId;
    // Endpoint di autorizzazione configurato per l'istanza Azure AD.
    private final String authority;
    // Segreto dell'applicazione usato per autenticarsi come client confidenziale.
    private final String clientSecret;

    // Costruttore che riceve i valori di configurazione dal file application.properties.
    public MsalClientProvider(
            // Iniezione del client ID dalla configurazione Spring.
            @Value("${security.azure.client-id}") String clientId,
            // Iniezione dell'authority (tenant e policy) dalla configurazione Spring.
            @Value("${security.azure.authority}") String authority,
            // Iniezione del client secret dalla configurazione Spring.
            @Value("${security.azure.client-secret}") String clientSecret) {
        // Assegnazione del client ID al campo della classe.
        this.clientId = clientId;
        // Assegnazione dell'authority al campo della classe.
        this.authority = authority;
        // Assegnazione del client secret al campo della classe.
        this.clientSecret = clientSecret;
    }

    // Metodo factory che costruisce un'istanza di ConfidentialClientApplication.
    public ConfidentialClientApplication createClient() throws MalformedURLException {
        // Creazione delle credenziali client partendo dal segreto configurato.
        IClientCredential credential = ClientCredentialFactory.createFromSecret(clientSecret);
        // Costruzione dell'applicazione confidenziale impostando client ID, credenziali e authority.
        return ConfidentialClientApplication.builder(clientId, credential)
                // Impostazione dell'URL dell'authority Azure AD.
                .authority(authority)
                // Finalizzazione della costruzione dell'oggetto client.
                .build();
    }

    // Metodo helper per ottenere un token di accesso per un determinato scope.
    public String acquireTokenForScope(String scope) throws ExecutionException, InterruptedException, MalformedURLException {
        // Creazione del client e avvio della richiesta di token.
        return createClient()
                // Preparazione della richiesta di token con i parametri dello scope.
                .acquireToken(com.microsoft.aad.msal4j.ClientCredentialParameters
                        // Configurazione dello scope richiesto come set di stringhe.
                        .builder(java.util.Set.of(scope))
                        // Costruzione dell'istanza di parametri della richiesta.
                        .build())
                // Attesa del completamento asincrono della richiesta.
                .get()
                // Estrazione del token di accesso dalla risposta.
                .accessToken();
    }
}
