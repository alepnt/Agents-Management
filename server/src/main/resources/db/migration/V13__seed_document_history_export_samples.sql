-- Dati fittizi per popolare lo storico documentale e facilitare i test di export
-- Le insert usano NOT EXISTS per evitare duplicati se la migrazione viene riapplicata in ambienti già popolati

-- Storico del contratto Helios Pharma
INSERT INTO "document_history" (document_type, document_id, action, description, created_at)
SELECT 'CONTRACT', c.id, 'CREATED', 'Contratto inserito a sistema per Helios Pharma', '2023-02-01T09:15:00'
FROM "contracts" c
WHERE c.customer_name = 'Helios Pharma S.p.A.'
  AND NOT EXISTS (
    SELECT 1 FROM "document_history" h
    WHERE h.document_type = 'CONTRACT' AND h.document_id = c.id AND h.action = 'CREATED'
);

INSERT INTO "document_history" (document_type, document_id, action, description, created_at)
SELECT 'CONTRACT', c.id, 'STATUS_CHANGED', 'Stato aggiornato su ACTIVE dopo firma cliente', '2023-02-01T09:45:00'
FROM "contracts" c
WHERE c.customer_name = 'Helios Pharma S.p.A.'
  AND NOT EXISTS (
    SELECT 1 FROM "document_history" h
    WHERE h.document_type = 'CONTRACT' AND h.document_id = c.id AND h.action = 'STATUS_CHANGED'
);

INSERT INTO "document_history" (document_type, document_id, action, description, created_at)
SELECT 'CONTRACT', c.id, 'UPDATED', 'Aggiornata descrizione con nuovi servizi cloud', '2023-03-15T11:30:00'
FROM "contracts" c
WHERE c.customer_name = 'Helios Pharma S.p.A.'
  AND NOT EXISTS (
    SELECT 1 FROM "document_history" h
    WHERE h.document_type = 'CONTRACT' AND h.document_id = c.id AND h.action = 'UPDATED'
);

-- Storico del contratto BlueGrape Retail
INSERT INTO "document_history" (document_type, document_id, action, description, created_at)
SELECT 'CONTRACT', c.id, 'CREATED', 'Contratto partner aperto per BlueGrape Retail', '2024-01-12T08:30:00'
FROM "contracts" c
WHERE c.customer_name = 'BlueGrape Retail'
  AND NOT EXISTS (
    SELECT 1 FROM "document_history" h
    WHERE h.document_type = 'CONTRACT' AND h.document_id = c.id AND h.action = 'CREATED'
);

INSERT INTO "document_history" (document_type, document_id, action, description, created_at)
SELECT 'CONTRACT', c.id, 'STATUS_CHANGED', 'Passaggio da DRAFT a ACTIVE dopo onboarding partner', '2024-01-12T09:00:00'
FROM "contracts" c
WHERE c.customer_name = 'BlueGrape Retail'
  AND NOT EXISTS (
    SELECT 1 FROM "document_history" h
    WHERE h.document_type = 'CONTRACT' AND h.document_id = c.id AND h.action = 'STATUS_CHANGED'
);

INSERT INTO "document_history" (document_type, document_id, action, description, created_at)
SELECT 'CONTRACT', c.id, 'UPDATED', 'Aggiornate condizioni commerciali per promozione Q2', '2024-02-20T10:15:00'
FROM "contracts" c
WHERE c.customer_name = 'BlueGrape Retail'
  AND NOT EXISTS (
    SELECT 1 FROM "document_history" h
    WHERE h.document_type = 'CONTRACT' AND h.document_id = c.id AND h.action = 'UPDATED'
);

-- Voci addizionali per le fatture, utili a testare filtri e export CSV
INSERT INTO "document_history" (document_type, document_id, action, description, created_at)
SELECT 'INVOICE', i.id, 'STATUS_CHANGED', 'Fattura inviata al cliente con stato SENT', '2024-04-11T10:50:00'
FROM "invoices" i
WHERE i.invoice_number = 'FA-2024-022'
  AND NOT EXISTS (
    SELECT 1 FROM "document_history" h
    WHERE h.document_type = 'INVOICE' AND h.document_id = i.id AND h.action = 'STATUS_CHANGED'
);

INSERT INTO "document_history" (document_type, document_id, action, description, created_at)
SELECT 'INVOICE', i.id, 'UPDATED', 'Note di pagamento aggiornate dopo richiesta cliente', '2024-07-15T14:10:00'
FROM "invoices" i
WHERE i.invoice_number = 'FA-2024-041'
  AND NOT EXISTS (
    SELECT 1 FROM "document_history" h
    WHERE h.document_type = 'INVOICE' AND h.document_id = i.id AND h.action = 'UPDATED'
);
