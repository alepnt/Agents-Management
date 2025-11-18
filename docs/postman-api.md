# Test delle API REST (Postman)

Questo README raccoglie in un unico posto tutte le chiamate REST esposte dal backend Spring Boot, con i percorsi da provare in Postman, metodo HTTP, parametri e payload richiesti.

## Autenticazione
- **POST /api/auth/login** – body JSON `{ accessToken, email, displayName, azureId }`.
- **POST /api/auth/register** – body JSON `{ azureId, email, displayName, agentCode, password, teamName?, roleName? }`.

## Articoli
- **GET /api/articles** – elenco.
- **GET /api/articles/{id}** – dettaglio.
- **POST /api/articles** – body JSON `ArticleDTO`.
- **PUT /api/articles/{id}** – body JSON `ArticleDTO`.
- **DELETE /api/articles/{id}**.

## Clienti
- **GET /api/customers** – elenco.
- **GET /api/customers/{id}** – dettaglio.
- **POST /api/customers** – body JSON `CustomerDTO`.
- **PUT /api/customers/{id}** – body JSON `CustomerDTO`.
- **DELETE /api/customers/{id}**.

## Contratti
- **GET /api/contracts** – elenco.
- **GET /api/contracts/{id}** – dettaglio.
- **POST /api/contracts** – body JSON `ContractDTO`.
- **PUT /api/contracts/{id}** – body JSON `ContractDTO`.
- **DELETE /api/contracts/{id}**.
- **GET /api/contracts/{id}/history** – storico modifiche del contratto.

## Fatture
- **GET /api/invoices** – elenco.
- **GET /api/invoices/{id}** – dettaglio.
- **POST /api/invoices** – body JSON `InvoiceDTO`.
- **PUT /api/invoices/{id}** – body JSON `InvoiceDTO`.
- **DELETE /api/invoices/{id}**.
- **POST /api/invoices/{id}/payments** – registra pagamento, body `{ amount, paymentDate, note }` (campi di `InvoicePaymentRequest`).
- **GET /api/invoices/{id}/history** – storico modifiche della fattura.

## Storico documenti
- **GET /api/history** – ricerca paginata con query param opzionali `documentType`, `documentId`, `actions`, `from`, `to`, `q`, `page`, `size`.
- **GET /api/history/export** – export CSV con stessi filtri (senza paginazione).
- **GET /api/contracts/{id}/history** / **GET /api/invoices/{id}/history** – cronologia per singolo documento (solo lettura).

> La creazione e l'aggiornamento dello storico sono automatici (non esistono endpoint `POST/PUT/DELETE` dedicati): gli eventi vengono registrati dai servizi di dominio in base alle operazioni eseguite su contratti e fatture.

## Notifiche
- **GET /api/notifications?userId={id}&since=...** – lista notifiche per utente (parametro `since` ISO opzionale).
- **GET /api/notifications/subscribe?userId={id}** – long polling per nuove notifiche.
- **POST /api/notifications** – body `{ userId?, teamId?, title, message }`.
- **POST /api/notifications/subscribe** – registra un canale push, body `{ userId, channel }`.

## Mail
- **POST /api/mail/send** – richiede header `Authorization: Bearer <token>`. Body `{ subject, body, to[], cc[], bcc[], attachments[] }`.

## Report
- **GET /api/reports/closed-invoices?from=YYYY-MM-DD&to=YYYY-MM-DD&agentId=...** – genera PDF con le fatture chiuse nel periodo (parametri opzionali).

## Statistiche
- **GET /api/stats/agent?year=YYYY** – KPI per singolo agente (anno opzionale).
- **GET /api/stats/team?year=YYYY** – KPI aggregati di team (anno opzionale).

## Chat
- **GET /api/chat/conversations?userId={id}** – conversazioni visibili all'utente.
- **GET /api/chat/messages?userId={id}&conversationId={cid}&since=...** – messaggi (parametro `since` ISO opzionale).
- **GET /api/chat/poll?userId={id}&conversationId={cid}** – long polling per nuovi messaggi.
- **POST /api/chat/messages** – body `{ senderId, conversationId, body }`.
