INSERT INTO roles (id, name) VALUES (1, 'Agent');

INSERT INTO teams (id, name) VALUES (1, 'Sales');
INSERT INTO teams (id, name) VALUES (2, 'Support');
INSERT INTO teams (id, name) VALUES (3, 'Vendite');

INSERT INTO users (id, azure_id, email, display_name, password_hash, role_id, team_id, active, created_at)
VALUES (1, 'azure-1', 'alice@example.com', 'Alice Agent', NULL, 1, 1, TRUE, TIMESTAMP '2023-01-01T00:00:00');
INSERT INTO users (id, azure_id, email, display_name, password_hash, role_id, team_id, active, created_at)
VALUES (2, 'azure-2', 'bob@example.com', 'Bob Agent', NULL, 1, 2, TRUE, TIMESTAMP '2023-01-01T00:00:00');

INSERT INTO agents (id, user_id, agent_code, team_role) VALUES (1, 1, 'A-001', 'Lead');
INSERT INTO agents (id, user_id, agent_code, team_role) VALUES (2, 2, 'A-002', 'Member');

INSERT INTO contracts (id, agent_id, customer_name, description, start_date, end_date, total_value, status)
VALUES (1, 1, 'Customer A', 'Contract A', DATE '2023-01-01', NULL, 1000.00, 'ACTIVE');
INSERT INTO contracts (id, agent_id, customer_name, description, start_date, end_date, total_value, status)
VALUES (2, 2, 'Customer B', 'Contract B', DATE '2023-01-01', NULL, 2000.00, 'ACTIVE');

INSERT INTO invoices (id, contract_id, invoice_number, customer_name, amount, issue_date, due_date, status, payment_date, notes, created_at)
VALUES (1, 1, 'INV-001', 'Customer A', 100.00, DATE '2023-12-15', DATE '2024-01-15', 'PAID', DATE '2024-01-10', NULL, TIMESTAMP '2024-01-10T00:00:00');
INSERT INTO invoices (id, contract_id, invoice_number, customer_name, amount, issue_date, due_date, status, payment_date, notes, created_at)
VALUES (2, 1, 'INV-002', 'Customer A', 250.00, DATE '2023-12-30', DATE '2024-02-15', 'PAID', DATE '2024-02-05', NULL, TIMESTAMP '2024-02-05T00:00:00');
INSERT INTO invoices (id, contract_id, invoice_number, customer_name, amount, issue_date, due_date, status, payment_date, notes, created_at)
VALUES (3, 2, 'INV-003', 'Customer B', 300.00, DATE '2024-02-10', DATE '2024-03-10', 'PAID', DATE '2024-03-20', NULL, TIMESTAMP '2024-03-20T00:00:00');
INSERT INTO invoices (id, contract_id, invoice_number, customer_name, amount, issue_date, due_date, status, payment_date, notes, created_at)
VALUES (4, 2, 'INV-004', 'Customer B', 400.00, DATE '2024-03-15', DATE '2024-04-15', 'SENT', NULL, NULL, TIMESTAMP '2024-03-15T00:00:00');
INSERT INTO invoices (id, contract_id, invoice_number, customer_name, amount, issue_date, due_date, status, payment_date, notes, created_at)
VALUES (5, 1, 'INV-005', 'Customer A', 500.00, DATE '2022-10-01', DATE '2022-11-01', 'PAID', DATE '2022-11-10', NULL, TIMESTAMP '2022-11-10T00:00:00');

INSERT INTO document_history (id, document_type, document_id, action, description, created_at)
VALUES (1, 'INVOICE', 10, 'CREATED', 'Invoice created', TIMESTAMP '2024-01-01T10:00:00');
INSERT INTO document_history (id, document_type, document_id, action, description, created_at)
VALUES (2, 'INVOICE', 10, 'UPDATED', 'Invoice updated', TIMESTAMP '2024-01-02T10:00:00');
INSERT INTO document_history (id, document_type, document_id, action, description, created_at)
VALUES (3, 'CONTRACT', 20, 'CREATED', 'Contract created', TIMESTAMP '2024-01-03T10:00:00');
INSERT INTO document_history (id, document_type, document_id, action, description, created_at)
VALUES (4, 'INVOICE', 11, 'PAYMENT_REGISTERED', 'Payment registered for invoice', TIMESTAMP '2024-02-01T10:00:00');
INSERT INTO document_history (id, document_type, document_id, action, description, created_at)
VALUES (5, 'INVOICE', 10, 'DELETED', 'Invoice deleted permanently', TIMESTAMP '2024-03-01T10:00:00');
