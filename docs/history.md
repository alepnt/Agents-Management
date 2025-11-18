# Gestione dello storico documentale

## Requisiti
- La cronologia dei documenti **non viene gestita manualmente via API**: è generata automaticamente dal backend in risposta alle operazioni sulle entità.
- Le azioni rilevanti vengono registrate dai servizi applicativi (ad es. `ContractService` e `InvoiceService`) attraverso `DocumentHistoryService.log(...)`, che persiste l'evento con tipo di documento, ID, azione e descrizione.
- L'esposizione esterna è **soltanto in lettura** tramite `/api/history` (ricerca paginata) e `/api/history/export` (export CSV); non sono previsti endpoint `POST/PUT/DELETE` per inserire o modificare lo storico.
- Per ottenere la cronologia specifica di un documento è possibile utilizzare anche gli endpoint `/api/contracts/{id}/history` e `/api/invoices/{id}/history`.

## Nota per eventuali estensioni
Qualora servissero operazioni CRUD esplicite sullo storico, andrebbe introdotto un `DocumentHistoryController` con metodi `POST/PUT/DELETE` e la logica corrispondente in `DocumentHistoryService`, abilitando la gestione manuale delle voci di cronologia.
