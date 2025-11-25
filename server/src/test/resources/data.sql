DELETE FROM "document_history";
DELETE FROM "invoices";
DELETE FROM "contracts";
DELETE FROM "agents";
DELETE FROM "users";
DELETE FROM "teams";
DELETE FROM "roles";

INSERT INTO "roles" (name) VALUES ('Agent');

INSERT INTO "teams" (name) VALUES ('Sales');
INSERT INTO "teams" (name) VALUES ('Support');
INSERT INTO "teams" (name) VALUES ('Vendite');

INSERT INTO "users" (azure_id, email, display_name, password_hash, role_id, team_id, active, created_at)
VALUES (
    'azure-1',
    'alice@example.com',
    'Alice Agent',
    NULL,
    (SELECT id FROM "roles" WHERE name = 'Agent'),
    (SELECT id FROM "teams" WHERE name = 'Sales'),
    TRUE,
    TIMESTAMP '2023-01-01T00:00:00'
);
INSERT INTO "users" (azure_id, email, display_name, password_hash, role_id, team_id, active, created_at)
VALUES (
    'azure-2',
    'bob@example.com',
    'Bob Agent',
    NULL,
    (SELECT id FROM "roles" WHERE name = 'Agent'),
    (SELECT id FROM "teams" WHERE name = 'Support'),
    TRUE,
    TIMESTAMP '2023-01-01T00:00:00'
);

INSERT INTO "agents" (user_id, agent_code, team_role)
VALUES (
    (SELECT id FROM "users" WHERE azure_id = 'azure-1'),
    'A-001',
    'Lead'
);
INSERT INTO "agents" (user_id, agent_code, team_role)
VALUES (
    (SELECT id FROM "users" WHERE azure_id = 'azure-2'),
    'A-002',
    'Member'
);

INSERT INTO "contracts" (agent_id, customer_name, description, start_date, end_date, total_value, status)
VALUES (
    (SELECT id FROM "agents" WHERE agent_code = 'A-001'),
    'Customer A',
    'Contract A',
    DATE '2023-01-01',
    NULL,
    1000.00,
    'ACTIVE'
);
INSERT INTO "contracts" (agent_id, customer_name, description, start_date, end_date, total_value, status)
VALUES (
    (SELECT id FROM "agents" WHERE agent_code = 'A-002'),
    'Customer B',
    'Contract B',
    DATE '2023-01-01',
    NULL,
    2000.00,
    'ACTIVE'
);

INSERT INTO "invoices" (contract_id, invoice_number, customer_name, amount, issue_date, due_date, status, payment_date, notes, created_at)
VALUES (
    (SELECT id FROM "contracts" WHERE customer_name = 'Customer A'),
    'INV-001',
    'Customer A',
    100.00,
    DATE '2023-12-15',
    DATE '2024-01-15',
    'PAID',
    DATE '2024-01-10',
    NULL,
    TIMESTAMP '2024-01-10T00:00:00'
);
INSERT INTO "invoices" (contract_id, invoice_number, customer_name, amount, issue_date, due_date, status, payment_date, notes, created_at)
VALUES (
    (SELECT id FROM "contracts" WHERE customer_name = 'Customer A'),
    'INV-002',
    'Customer A',
    250.00,
    DATE '2023-12-30',
    DATE '2024-02-15',
    'PAID',
    DATE '2024-02-05',
    NULL,
    TIMESTAMP '2024-02-05T00:00:00'
);
INSERT INTO "invoices" (contract_id, invoice_number, customer_name, amount, issue_date, due_date, status, payment_date, notes, created_at)
VALUES (
    (SELECT id FROM "contracts" WHERE customer_name = 'Customer B'),
    'INV-003',
    'Customer B',
    300.00,
    DATE '2024-02-10',
    DATE '2024-03-10',
    'PAID',
    DATE '2024-03-20',
    NULL,
    TIMESTAMP '2024-03-20T00:00:00'
);
INSERT INTO "invoices" (contract_id, invoice_number, customer_name, amount, issue_date, due_date, status, payment_date, notes, created_at)
VALUES (
    (SELECT id FROM "contracts" WHERE customer_name = 'Customer B'),
    'INV-004',
    'Customer B',
    400.00,
    DATE '2024-03-15',
    DATE '2024-04-15',
    'SENT',
    NULL,
    NULL,
    TIMESTAMP '2024-03-15T00:00:00'
);
INSERT INTO "invoices" (contract_id, invoice_number, customer_name, amount, issue_date, due_date, status, payment_date, notes, created_at)
VALUES (
    (SELECT id FROM "contracts" WHERE customer_name = 'Customer A'),
    'INV-005',
    'Customer A',
    500.00,
    DATE '2022-10-01',
    DATE '2022-11-01',
    'PAID',
    DATE '2022-11-10',
    NULL,
    TIMESTAMP '2022-11-10T00:00:00'
);

INSERT INTO "document_history" (document_type, document_id, action, description, created_at)
VALUES (
    'INVOICE',
    (SELECT id FROM "invoices" WHERE invoice_number = 'INV-001'),
    'CREATED',
    'Invoice created',
    TIMESTAMP '2024-01-01T10:00:00'
);
INSERT INTO "document_history" (document_type, document_id, action, description, created_at)
VALUES (
    'INVOICE',
    (SELECT id FROM "invoices" WHERE invoice_number = 'INV-001'),
    'UPDATED',
    'Invoice updated',
    TIMESTAMP '2024-01-02T10:00:00'
);
INSERT INTO "document_history" (document_type, document_id, action, description, created_at)
VALUES (
    'CONTRACT',
    (SELECT id FROM "contracts" WHERE customer_name = 'Customer A'),
    'CREATED',
    'Contract created',
    TIMESTAMP '2024-01-03T10:00:00'
);
INSERT INTO "document_history" (document_type, document_id, action, description, created_at)
VALUES (
    'INVOICE',
    (SELECT id FROM "invoices" WHERE invoice_number = 'INV-002'),
    'PAYMENT_REGISTERED',
    'Payment registered for invoice',
    TIMESTAMP '2024-02-01T10:00:00'
);
INSERT INTO "document_history" (document_type, document_id, action, description, created_at)
VALUES (
    'INVOICE',
    (SELECT id FROM "invoices" WHERE invoice_number = 'INV-001'),
    'DELETED',
    'Invoice deleted permanently',
    TIMESTAMP '2024-03-01T10:00:00'
);
