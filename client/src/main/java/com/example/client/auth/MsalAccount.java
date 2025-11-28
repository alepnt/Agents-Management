package com.example.client.auth; // Package dedicato ai componenti di autenticazione lato client.

/**
 * Rappresenta un account MSAL (Microsoft Authentication Library)
 * ottenuto tramite autenticazione Azure AD.
 * Include informazioni utili alla gestione dell'identità dell’utente.
 */
public record MsalAccount( // Record immutabile che modella un profilo utente MSAL.
                String username, // Username dell’account (UPN Azure AD).
                String displayName, // Nome visualizzato dell’utente.
                String objectId, // Object ID univoco dell’utente in Azure AD.
                String tenantId, // Identificatore del tenant Azure AD.
                String homeAccountId // ID univoco cross-tenant usato da MSAL.
) {
} // Fine del record MsalAccount.
