INSERT INTO "roles" (name)
SELECT 'Admin' WHERE NOT EXISTS (SELECT 1 FROM "roles" WHERE name = 'Admin');
INSERT INTO "roles" (name)
SELECT 'Manager' WHERE NOT EXISTS (SELECT 1 FROM "roles" WHERE name = 'Manager');
INSERT INTO "roles" (name)
SELECT 'Agent' WHERE NOT EXISTS (SELECT 1 FROM "roles" WHERE name = 'Agent');

INSERT INTO "teams" (name)
SELECT 'Vendite' WHERE NOT EXISTS (SELECT 1 FROM "teams" WHERE name = 'Vendite');
INSERT INTO "teams" (name)
SELECT 'Enterprise' WHERE NOT EXISTS (SELECT 1 FROM "teams" WHERE name = 'Enterprise');
INSERT INTO "teams" (name)
SELECT 'Partner Channel' WHERE NOT EXISTS (SELECT 1 FROM "teams" WHERE name = 'Partner Channel');

INSERT INTO "users" (azure_id, email, display_name, password_hash, role_id, team_id, active)
VALUES
('az-admin-001', 'alessia.riva@gestoreagenti.local', 'Alessia Riva', '$2a$10$wqCjCrdPBwuvMCH.xpLk4e2uqm2m4TTFg6uUKQLyQYJgpOlD6Gvbi',
 (SELECT id FROM "roles" WHERE name = 'Admin'), (SELECT id FROM "teams" WHERE name = 'Vendite'), 1),
('az-mgr-001', 'marco.venturi@gestoreagenti.local', 'Marco Venturi', '$2a$10$C3f9Ew7mEuJnlz6ro9lpCOHT5J61J3o/F3uMxst3WRHQ2qP7hZ4u2',
 (SELECT id FROM "roles" WHERE name = 'Manager'), (SELECT id FROM "teams" WHERE name = 'Vendite'), 1),
('az-ag-001', 'luca.ferri@gestoreagenti.local', 'Luca Ferri', '$2a$10$O30tgOlPZ7UfAq5Jovg1JeNw2N5YI6TcEF/8ZbhvACyMfmB34lTgS',
 (SELECT id FROM "roles" WHERE name = 'Agent'), (SELECT id FROM "teams" WHERE name = 'Vendite'), 1),
('az-ag-002', 'martina.conti@gestoreagenti.local', 'Martina Conti', '$2a$10$O30tgOlPZ7UfAq5Jovg1JeNw2N5YI6TcEF/8ZbhvACyMfmB34lTgS',
 (SELECT id FROM "roles" WHERE name = 'Agent'), (SELECT id FROM "teams" WHERE name = 'Vendite'), 1),
('az-ag-003', 'sergio.leoni@gestoreagenti.local', 'Sergio Leoni', '$2a$10$O30tgOlPZ7UfAq5Jovg1JeNw2N5YI6TcEF/8ZbhvACyMfmB34lTgS',
 (SELECT id FROM "roles" WHERE name = 'Agent'), (SELECT id FROM "teams" WHERE name = 'Enterprise'), 1),
('az-ag-004', 'giulia.bianchi@gestoreagenti.local', 'Giulia Bianchi', '$2a$10$O30tgOlPZ7UfAq5Jovg1JeNw2N5YI6TcEF/8ZbhvACyMfmB34lTgS',
 (SELECT id FROM "roles" WHERE name = 'Agent'), (SELECT id FROM "teams" WHERE name = 'Partner Channel'), 1),
('az-ag-005', 'paolo.serra@gestoreagenti.local', 'Paolo Serra', '$2a$10$O30tgOlPZ7UfAq5Jovg1JeNw2N5YI6TcEF/8ZbhvACyMfmB34lTgS',
 (SELECT id FROM "roles" WHERE name = 'Agent'), (SELECT id FROM "teams" WHERE name = 'Enterprise'), 1),
('az-ag-006', 'elisa.romano@gestoreagenti.local', 'Elisa Romano', '$2a$10$O30tgOlPZ7UfAq5Jovg1JeNw2N5YI6TcEF/8ZbhvACyMfmB34lTgS',
 (SELECT id FROM "roles" WHERE name = 'Agent'), (SELECT id FROM "teams" WHERE name = 'Partner Channel'), 1);

INSERT INTO "agents" (user_id, agent_code, team_role)
SELECT u.id, 'AG001', 'Senior Agent'
FROM "users" u WHERE u.email = 'luca.ferri@gestoreagenti.local'
    AND NOT EXISTS (SELECT 1 FROM "agents" a WHERE a.agent_code = 'AG001');
INSERT INTO "agents" (user_id, agent_code, team_role)
SELECT u.id, 'AG002', 'Account Executive'
FROM "users" u WHERE u.email = 'martina.conti@gestoreagenti.local'
    AND NOT EXISTS (SELECT 1 FROM "agents" a WHERE a.agent_code = 'AG002');
INSERT INTO "agents" (user_id, agent_code, team_role)
SELECT u.id, 'AG003', 'Enterprise Closer'
FROM "users" u WHERE u.email = 'sergio.leoni@gestoreagenti.local'
    AND NOT EXISTS (SELECT 1 FROM "agents" a WHERE a.agent_code = 'AG003');
INSERT INTO "agents" (user_id, agent_code, team_role)
SELECT u.id, 'AG004', 'Partner Specialist'
FROM "users" u WHERE u.email = 'giulia.bianchi@gestoreagenti.local'
    AND NOT EXISTS (SELECT 1 FROM "agents" a WHERE a.agent_code = 'AG004');
INSERT INTO "agents" (user_id, agent_code, team_role)
SELECT u.id, 'AG005', 'Solution Advisor'
FROM "users" u WHERE u.email = 'paolo.serra@gestoreagenti.local'
    AND NOT EXISTS (SELECT 1 FROM "agents" a WHERE a.agent_code = 'AG005');
INSERT INTO "agents" (user_id, agent_code, team_role)
SELECT u.id, 'AG006', 'Territory Rep'
FROM "users" u WHERE u.email = 'elisa.romano@gestoreagenti.local'
    AND NOT EXISTS (SELECT 1 FROM "agents" a WHERE a.agent_code = 'AG006');

INSERT INTO "customers" (name, vat_number, tax_code, email, phone, address)
VALUES
('Farmacia San Carlo', 'IT09876543210', 'FMSC12345A', 'acquisti@sancarlofarmacia.it', '+39-011-223344', 'Via Roma 12, Torino'),
('Helios Pharma S.p.A.', 'IT11223344556', 'HLPH99887B', 'procurement@heliospharma.com', '+39-02-99334455', 'Viale Lombardia 45, Milano'),
('RetailUp SRL', 'IT55667788990', 'RTUP33322C', 'orders@retailup.it', '+39-06-44332211', 'Via Appia 203, Roma'),
('LineaVerde Market', 'IT66778899001', 'LVMT55661D', 'acquisti@lineaverde.com', '+39-030-112233', 'Via Trento 5, Brescia'),
('TechBridge Solutions', 'IT77889900112', 'TBRS88442E', 'finance@techbridge.solutions', '+39-051-667788', 'Via Emilia 77, Bologna'),
('NordEst Medical', 'IT88990011223', 'NEMC77553F', 'contabilita@nordestmedical.it', '+39-041-223344', 'Fondamenta 401, Venezia'),
('BlueGrape Retail', 'IT99001122334', 'BGRT66444G', 'billing@bluegrape-retail.it', '+39-081-778899', 'Via Toledo 88, Napoli'),
('LogiChain B2B', 'IT10111213141', 'LCBB55221H', 'ap@logichain.eu', '+39-055-667788', 'Via Dante 14, Firenze'),
('Caffe'' Aurora', 'IT12131415161', 'CFAR44332I', 'pagamenti@caffeaurora.it', '+39-091-556677', 'Via Liberta'' 33, Palermo'),
('GreenPulse Energy', 'IT13141516171', 'GPEN22331L', 'accounts@greenpulse.energy', '+39-02-4445566', 'Via Monte Rosa 91, Milano'),
('Ottica VisionPiu''', 'IT14151617181', 'OTVP66772M', 'amministrazione@visionpiu.it', '+39-0131-112233', 'Corso Alfieri 20, Asti'),
('MondoPet Retail', 'IT15161718191', 'MPET99883N', 'finance@mondopet.it', '+39-0721-334455', 'Via Nazionale 18, Pesaro'),
('CosmoBeauty S.p.A.', 'IT16171819202', 'CBEA22114O', 'contatti@cosmobeauty.it', '+39-06-66778899', 'Via Tuscolana 300, Roma'),
('Brixia Instruments', 'IT17181920212', 'BRXI88554P', 'orders@brixiainstruments.com', '+39-030-665544', 'Via Triumplina 55, Brescia'),
('Latte&Co Distribuzione', 'IT18192021222', 'LTCO77445Q', 'amministrazione@latte-co.it', '+39-0171-223344', 'Via Cuneo 8, Cuneo');

INSERT INTO "contracts" (agent_id, customer_name, description, start_date, end_date, total_value, status)
VALUES
((SELECT id FROM "agents" WHERE agent_code = 'AG001'), 'Helios Pharma S.p.A.', 'Distribuzione regionale linee specialistiche', '2023-02-01', NULL, 850000.00, 'ACTIVE'),
((SELECT id FROM "agents" WHERE agent_code = 'AG002'), 'RetailUp SRL', 'Piano loyalty e gift card GDO', '2023-05-15', NULL, 320000.00, 'ACTIVE'),
((SELECT id FROM "agents" WHERE agent_code = 'AG003'), 'GreenPulse Energy', 'Soluzioni cloud e monitoraggio energetico', '2022-11-10', NULL, 1250000.00, 'ACTIVE'),
((SELECT id FROM "agents" WHERE agent_code = 'AG004'), 'BlueGrape Retail', 'Canale partner per Nord Italia', '2024-01-12', NULL, 410000.00, 'ACTIVE'),
((SELECT id FROM "agents" WHERE agent_code = 'AG005'), 'TechBridge Solutions', 'Suite CRM e onboarding team field', '2023-08-01', NULL, 275000.00, 'ACTIVE'),
((SELECT id FROM "agents" WHERE agent_code = 'AG006'), 'Farmacia San Carlo', 'Forniture stagionali OTC', '2021-09-01', '2023-08-31', 180000.00, 'EXPIRED'),
((SELECT id FROM "agents" WHERE agent_code = 'AG002'), 'Ottica VisionPiu''', 'Gestione pagamenti dilazionati ottica', '2022-03-05', '2023-03-04', 95000.00, 'EXPIRED');

INSERT INTO "articles" (code, name, unit_price, vat_rate, unit_of_measure, description)
VALUES
('CONS-SaaS', 'Consulenza SaaS', 1500.00, 22.00, 'ore', 'Attivita'' consulenziale su piattaforma cloud'),
('LIC-CRM', 'Licenza CRM annuale', 12000.00, 22.00, 'licenza', 'Licenza GestoreAgenti per 12 mesi'),
('SERV-ONB', 'Pacchetto onboarding', 4500.00, 22.00, 'pacchetto', 'Setup iniziale e training team'),
('HARD-GTW', 'Gateway IoT', 890.00, 22.00, 'pezzo', 'Gateway per sensori energetici'),
('SUP-PRIO', 'Supporto prioritario', 750.00, 22.00, 'mese', 'Supporto dedicato H24');

INSERT INTO "invoices" (contract_id, invoice_number, customer_id, customer_name, amount, issue_date, due_date, status, payment_date, notes, created_at)
VALUES
((SELECT id FROM "contracts" WHERE customer_name = 'Helios Pharma S.p.A.'), 'FA-2024-001', (SELECT id FROM "customers" WHERE name = 'Helios Pharma S.p.A.'), 'Helios Pharma S.p.A.', 18500.00, '2024-01-10', '2024-02-09', 'PAID', '2024-02-02', 'Canone Q1', '2024-01-10T10:00:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'Helios Pharma S.p.A.'), 'FA-2024-006', (SELECT id FROM "customers" WHERE name = 'Helios Pharma S.p.A.'), 'Helios Pharma S.p.A.', 19400.00, '2024-02-10', '2024-03-11', 'PAID', '2024-03-05', 'Licenze aggiuntive', '2024-02-10T09:45:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'Helios Pharma S.p.A.'), 'FA-2024-012', (SELECT id FROM "customers" WHERE name = 'Helios Pharma S.p.A.'), 'Helios Pharma S.p.A.', 20500.00, '2024-03-12', '2024-04-11', 'PAID', '2024-04-02', 'Servizi professionali', '2024-03-12T11:10:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'Helios Pharma S.p.A.'), 'FA-2024-018', (SELECT id FROM "customers" WHERE name = 'Helios Pharma S.p.A.'), 'Helios Pharma S.p.A.', 18600.00, '2024-04-10', '2024-05-10', 'PAID', '2024-05-03', 'Canone Q2', '2024-04-10T10:20:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'Helios Pharma S.p.A.'), 'FA-2024-030', (SELECT id FROM "customers" WHERE name = 'Helios Pharma S.p.A.'), 'Helios Pharma S.p.A.', 19800.00, '2024-06-12', '2024-07-12', 'PAID', '2024-07-05', 'Servizi consulenza', '2024-06-12T09:30:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'Helios Pharma S.p.A.'), 'FA-2024-036', (SELECT id FROM "customers" WHERE name = 'Helios Pharma S.p.A.'), 'Helios Pharma S.p.A.', 0.00, '2024-07-10', '2024-08-09', 'CANCELLED', NULL, 'Nota di accredito setup', '2024-07-10T08:55:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'Helios Pharma S.p.A.'), 'FA-2024-042', (SELECT id FROM "customers" WHERE name = 'Helios Pharma S.p.A.'), 'Helios Pharma S.p.A.', 20750.00, '2024-08-10', '2024-09-09', 'SENT', NULL, 'Canone Q3', '2024-08-10T10:15:00'),

((SELECT id FROM "contracts" WHERE customer_name = 'RetailUp SRL'), 'FA-2024-002', (SELECT id FROM "customers" WHERE name = 'RetailUp SRL'), 'RetailUp SRL', 9200.00, '2024-01-15', '2024-02-14', 'PAID', '2024-02-07', 'Lancio loyalty nord', '2024-01-15T10:30:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'RetailUp SRL'), 'FA-2024-007', (SELECT id FROM "customers" WHERE name = 'RetailUp SRL'), 'RetailUp SRL', 9800.00, '2024-02-18', '2024-03-19', 'PAID', '2024-03-12', 'Gift card trimestrali', '2024-02-18T09:40:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'RetailUp SRL'), 'FA-2024-013', (SELECT id FROM "customers" WHERE name = 'RetailUp SRL'), 'RetailUp SRL', 10150.00, '2024-03-20', '2024-04-19', 'PAID', '2024-04-09', 'Campagna centrale', '2024-03-20T12:05:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'RetailUp SRL'), 'FA-2024-019', (SELECT id FROM "customers" WHERE name = 'RetailUp SRL'), 'RetailUp SRL', 9450.00, '2024-04-18', '2024-05-18', 'PAID', '2024-05-08', 'Aggiornamento API', '2024-04-18T10:55:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'RetailUp SRL'), 'FA-2024-025', (SELECT id FROM "customers" WHERE name = 'RetailUp SRL'), 'RetailUp SRL', 9700.00, '2024-05-22', '2024-06-21', 'SENT', NULL, 'Nuove sedi retail', '2024-05-22T09:20:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'RetailUp SRL'), 'FA-2024-031', (SELECT id FROM "customers" WHERE name = 'RetailUp SRL'), 'RetailUp SRL', 0.00, '2024-06-24', '2024-07-24', 'CANCELLED', NULL, 'Ordine cancellato dal cliente', '2024-06-24T08:50:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'RetailUp SRL'), 'FA-2024-037', (SELECT id FROM "customers" WHERE name = 'RetailUp SRL'), 'RetailUp SRL', 10300.00, '2024-07-22', '2024-08-21', 'PAID', '2024-08-12', 'Lancio gift Q3', '2024-07-22T10:45:00'),

((SELECT id FROM "contracts" WHERE customer_name = 'GreenPulse Energy'), 'FA-2024-003', (SELECT id FROM "customers" WHERE name = 'GreenPulse Energy'), 'GreenPulse Energy', 48200.00, '2024-01-08', '2024-02-07', 'PAID', '2024-01-29', 'Hardware gateway e licenze', '2024-01-08T09:10:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'GreenPulse Energy'), 'FA-2024-008', (SELECT id FROM "customers" WHERE name = 'GreenPulse Energy'), 'GreenPulse Energy', 15600.00, '2024-02-05', '2024-03-06', 'PAID', '2024-02-27', 'Servizi onboarding plant', '2024-02-05T10:40:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'GreenPulse Energy'), 'FA-2024-014', (SELECT id FROM "customers" WHERE name = 'GreenPulse Energy'), 'GreenPulse Energy', 16800.00, '2024-03-07', '2024-04-06', 'PAID', '2024-03-29', 'Supporto prioritario', '2024-03-07T09:05:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'GreenPulse Energy'), 'FA-2024-020', (SELECT id FROM "customers" WHERE name = 'GreenPulse Energy'), 'GreenPulse Energy', 17500.00, '2024-04-09', '2024-05-09', 'PAID', '2024-05-02', 'Gateway aggiuntivi', '2024-04-09T11:15:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'GreenPulse Energy'), 'FA-2024-026', (SELECT id FROM "customers" WHERE name = 'GreenPulse Energy'), 'GreenPulse Energy', 16200.00, '2024-05-14', '2024-06-13', 'PAID', '2024-06-05', 'Canone monitoraggio', '2024-05-14T10:25:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'GreenPulse Energy'), 'FA-2024-032', (SELECT id FROM "customers" WHERE name = 'GreenPulse Energy'), 'GreenPulse Energy', 16900.00, '2024-06-10', '2024-07-10', 'PAID', '2024-07-03', 'Upgrade firmware', '2024-06-10T09:50:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'GreenPulse Energy'), 'FA-2024-044', (SELECT id FROM "customers" WHERE name = 'GreenPulse Energy'), 'GreenPulse Energy', 17750.00, '2024-08-05', '2024-09-04', 'PAID', '2024-08-27', 'Canone estate', '2024-08-05T10:05:00'),

((SELECT id FROM "contracts" WHERE customer_name = 'BlueGrape Retail'), 'FA-2024-004', (SELECT id FROM "customers" WHERE name = 'BlueGrape Retail'), 'BlueGrape Retail', 7800.00, '2024-01-20', '2024-02-19', 'PAID', '2024-02-10', 'Setup canale nord', '2024-01-20T10:00:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'BlueGrape Retail'), 'FA-2024-009', (SELECT id FROM "customers" WHERE name = 'BlueGrape Retail'), 'BlueGrape Retail', 8100.00, '2024-02-21', '2024-03-22', 'PAID', '2024-03-12', 'Campagna promo Q1', '2024-02-21T11:30:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'BlueGrape Retail'), 'FA-2024-015', (SELECT id FROM "customers" WHERE name = 'BlueGrape Retail'), 'BlueGrape Retail', 8400.00, '2024-03-24', '2024-04-23', 'SENT', NULL, 'Nuove aperture', '2024-03-24T09:25:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'BlueGrape Retail'), 'FA-2024-021', (SELECT id FROM "customers" WHERE name = 'BlueGrape Retail'), 'BlueGrape Retail', 8250.00, '2024-04-26', '2024-05-26', 'PAID', '2024-05-17', 'Rinnovo canone', '2024-04-26T10:35:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'BlueGrape Retail'), 'FA-2024-039', (SELECT id FROM "customers" WHERE name = 'BlueGrape Retail'), 'BlueGrape Retail', 8750.00, '2024-07-31', '2024-08-30', 'PAID', '2024-08-21', 'Promo ferragosto', '2024-07-31T11:05:00'),

((SELECT id FROM "contracts" WHERE customer_name = 'TechBridge Solutions'), 'FA-2024-005', (SELECT id FROM "customers" WHERE name = 'TechBridge Solutions'), 'TechBridge Solutions', 11500.00, '2024-01-05', '2024-02-04', 'PAID', '2024-01-26', 'CRM rollout', '2024-01-05T10:15:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'TechBridge Solutions'), 'FA-2024-010', (SELECT id FROM "customers" WHERE name = 'TechBridge Solutions'), 'TechBridge Solutions', 11800.00, '2024-02-06', '2024-03-07', 'PAID', '2024-02-28', 'Integrazione ERP', '2024-02-06T11:25:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'TechBridge Solutions'), 'FA-2024-016', (SELECT id FROM "customers" WHERE name = 'TechBridge Solutions'), 'TechBridge Solutions', 11200.00, '2024-03-08', '2024-04-07', 'PAID', '2024-03-29', 'Formazione avanzata', '2024-03-08T09:35:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'TechBridge Solutions'), 'FA-2024-022', (SELECT id FROM "customers" WHERE name = 'TechBridge Solutions'), 'TechBridge Solutions', 10950.00, '2024-04-11', '2024-05-11', 'SENT', NULL, 'Supporto dedicato', '2024-04-11T10:45:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'TechBridge Solutions'), 'FA-2024-028', (SELECT id FROM "customers" WHERE name = 'TechBridge Solutions'), 'TechBridge Solutions', 0.00, '2024-05-13', '2024-06-12', 'CANCELLED', NULL, 'Rettifica ordine', '2024-05-13T08:45:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'TechBridge Solutions'), 'FA-2024-034', (SELECT id FROM "customers" WHERE name = 'TechBridge Solutions'), 'TechBridge Solutions', 12100.00, '2024-06-15', '2024-07-15', 'PAID', '2024-07-08', 'Upgrade licenze', '2024-06-15T10:55:00'),

((SELECT id FROM "contracts" WHERE customer_name = 'Farmacia San Carlo'), 'FA-2024-011', (SELECT id FROM "customers" WHERE name = 'Farmacia San Carlo'), 'Farmacia San Carlo', 4200.00, '2024-02-02', '2024-03-03', 'PAID', '2024-02-24', 'Campagna OTC inverno', '2024-02-02T10:05:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'Farmacia San Carlo'), 'FA-2024-023', (SELECT id FROM "customers" WHERE name = 'Farmacia San Carlo'), 'Farmacia San Carlo', 4350.00, '2024-04-06', '2024-05-06', 'PAID', '2024-04-28', 'Fornitura primavera', '2024-04-06T11:40:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'Farmacia San Carlo'), 'FA-2024-029', (SELECT id FROM "customers" WHERE name = 'Farmacia San Carlo'), 'Farmacia San Carlo', 4520.00, '2024-05-08', '2024-06-07', 'PAID', '2024-05-30', 'Fornitura estate', '2024-05-08T10:50:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'Farmacia San Carlo'), 'FA-2024-035', (SELECT id FROM "customers" WHERE name = 'Farmacia San Carlo'), 'Farmacia San Carlo', 0.00, '2024-06-09', '2024-07-09', 'CANCELLED', NULL, 'Nota di accredito', '2024-06-09T09:55:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'Farmacia San Carlo'), 'FA-2024-041', (SELECT id FROM "customers" WHERE name = 'Farmacia San Carlo'), 'Farmacia San Carlo', 4380.00, '2024-07-12', '2024-08-11', 'PAID', '2024-08-03', 'Fornitura estiva', '2024-07-12T10:35:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'Farmacia San Carlo'), 'FA-2024-047', (SELECT id FROM "customers" WHERE name = 'Farmacia San Carlo'), 'Farmacia San Carlo', 4680.00, '2024-08-14', '2024-09-13', 'SENT', NULL, 'Fornitura autunno', '2024-08-14T11:45:00'),

((SELECT id FROM "contracts" WHERE customer_name = 'Ottica VisionPiu'''), 'FA-2024-048', (SELECT id FROM "customers" WHERE name = 'Ottica VisionPiu'''), 'Ottica VisionPiu''', 5100.00, '2024-02-12', '2024-03-13', 'PAID', '2024-03-04', 'Rinnovo mensile', '2024-02-12T10:25:00'),
((SELECT id FROM "contracts" WHERE customer_name = 'Ottica VisionPiu'''), 'FA-2024-049', (SELECT id FROM "customers" WHERE name = 'Ottica VisionPiu'''), 'Ottica VisionPiu''', 0.00, '2024-03-14', '2024-04-13', 'CANCELLED', NULL, 'Contratto in chiusura', '2024-03-14T09:15:00');

INSERT INTO "invoice_lines" (invoice_id, article_id, article_code, description, quantity, unit_price, vat_rate, total)
SELECT i.id, (SELECT id FROM "articles" WHERE code = 'LIC-CRM'), 'LIC-CRM', 'Licenza annuale CRM', 1, 12000.00, 22.00, 12000.00 FROM "invoices" i WHERE i.invoice_number IN ('FA-2024-001','FA-2024-006','FA-2024-012','FA-2024-018','FA-2024-030','FA-2024-042');
INSERT INTO "invoice_lines" (invoice_id, article_id, article_code, description, quantity, unit_price, vat_rate, total)
SELECT i.id, (SELECT id FROM "articles" WHERE code = 'CONS-SaaS'), 'CONS-SaaS', 'Consulenza personalizzata', 4, 1500.00, 22.00, 6000.00 FROM "invoices" i WHERE i.invoice_number IN ('FA-2024-001','FA-2024-006','FA-2024-012','FA-2024-018','FA-2024-030');
INSERT INTO "invoice_lines" (invoice_id, article_id, article_code, description, quantity, unit_price, vat_rate, total)
SELECT i.id, (SELECT id FROM "articles" WHERE code = 'SERV-ONB'), 'SERV-ONB', 'Onboarding team e training', 1, 4500.00, 22.00, 4500.00 FROM "invoices" i WHERE i.invoice_number IN ('FA-2024-002','FA-2024-005','FA-2024-010','FA-2024-016','FA-2024-022','FA-2024-026','FA-2024-034');
INSERT INTO "invoice_lines" (invoice_id, article_id, article_code, description, quantity, unit_price, vat_rate, total)
SELECT i.id, (SELECT id FROM "articles" WHERE code = 'HARD-GTW'), 'HARD-GTW', 'Gateway IoT industriali', 10, 890.00, 22.00, 8900.00 FROM "invoices" i WHERE i.invoice_number IN ('FA-2024-003','FA-2024-008','FA-2024-014','FA-2024-020','FA-2024-026','FA-2024-032','FA-2024-044');
INSERT INTO "invoice_lines" (invoice_id, article_id, article_code, description, quantity, unit_price, vat_rate, total)
SELECT i.id, (SELECT id FROM "articles" WHERE code = 'SUP-PRIO'), 'SUP-PRIO', 'Supporto prioritario mensile', 3, 750.00, 22.00, 2250.00 FROM "invoices" i WHERE i.invoice_number IN ('FA-2024-009','FA-2024-015','FA-2024-021','FA-2024-039');
INSERT INTO "invoice_lines" (invoice_id, article_code, description, quantity, unit_price, vat_rate, total)
VALUES
((SELECT id FROM "invoices" WHERE invoice_number = 'FA-2024-025'), 'LIC-CRM', 'Licenze retail nuove aperture', 1, 9700.00, 22.00, 9700.00),
((SELECT id FROM "invoices" WHERE invoice_number = 'FA-2024-031'), 'LIC-CRM', 'Ordine annullato', 1, 0.00, 22.00, 0.00),
((SELECT id FROM "invoices" WHERE invoice_number = 'FA-2024-037'), 'LIC-CRM', 'Canone trimestre Q3', 1, 10300.00, 22.00, 10300.00),
((SELECT id FROM "invoices" WHERE invoice_number = 'FA-2024-004'), 'CONS-SaaS', 'Workshop canale partner', 2, 1500.00, 22.00, 3000.00),
((SELECT id FROM "invoices" WHERE invoice_number = 'FA-2024-015'), 'CONS-SaaS', 'Pianificazione aperture', 2, 1500.00, 22.00, 3000.00),
((SELECT id FROM "invoices" WHERE invoice_number = 'FA-2024-021'), 'CONS-SaaS', 'Strategia promo Q2', 1, 1500.00, 22.00, 1500.00),
((SELECT id FROM "invoices" WHERE invoice_number = 'FA-2024-039'), 'CONS-SaaS', 'Campagna ferragosto', 1, 1500.00, 22.00, 1500.00),
((SELECT id FROM "invoices" WHERE invoice_number = 'FA-2024-023'), 'SUP-PRIO', 'Supporto farmacia', 2, 750.00, 22.00, 1500.00),
((SELECT id FROM "invoices" WHERE invoice_number = 'FA-2024-029'), 'SUP-PRIO', 'Aggiornamento catalogo', 2, 750.00, 22.00, 1500.00),
((SELECT id FROM "invoices" WHERE invoice_number = 'FA-2024-035'), 'SUP-PRIO', 'Nota di accredito', 1, 0.00, 22.00, 0.00),
((SELECT id FROM "invoices" WHERE invoice_number = 'FA-2024-041'), 'SUP-PRIO', 'Supporto estivo', 2, 750.00, 22.00, 1500.00),
((SELECT id FROM "invoices" WHERE invoice_number = 'FA-2024-047'), 'SUP-PRIO', 'Supporto autunnale', 2, 750.00, 22.00, 1500.00),
((SELECT id FROM "invoices" WHERE invoice_number = 'FA-2024-011'), 'SERV-ONB', 'Campagna inverno', 1, 4200.00, 22.00, 4200.00),
((SELECT id FROM "invoices" WHERE invoice_number = 'FA-2024-048'), 'LIC-CRM', 'Rinnovo ottica', 1, 5100.00, 22.00, 5100.00),
((SELECT id FROM "invoices" WHERE invoice_number = 'FA-2024-049'), 'LIC-CRM', 'Contratto chiuso', 1, 0.00, 22.00, 0.00);

INSERT INTO "document_history" (document_type, document_id, action, description)
SELECT 'INVOICE', i.id, 'CREATED', 'Documento creato'
FROM "invoices" i;

INSERT INTO "document_history" (document_type, document_id, action, description)
SELECT 'INVOICE', i.id, 'APPROVED', 'Documento approvato'
FROM "invoices" i
WHERE i.status <> 'CANCELLED';

INSERT INTO "document_history" (document_type, document_id, action, description)
SELECT 'INVOICE', i.id, 'PAYMENT_REGISTERED', 'Pagamento registrato'
FROM "invoices" i
WHERE i.status = 'PAID';

INSERT INTO "commissions" (agent_id, contract_id, total_commission, paid_commission, pending_commission, last_updated)
SELECT c.agent_id,
       c.id,
       SUM(CASE WHEN i.status = 'PAID' THEN i.amount * 0.08 ELSE 0 END),
       SUM(CASE WHEN i.status = 'PAID' THEN i.amount * 0.08 ELSE 0 END),
       SUM(CASE WHEN i.status = 'SENT' THEN i.amount * 0.08 ELSE 0 END),
       CURRENT_TIMESTAMP
FROM "contracts" c
LEFT JOIN "invoices" i ON i.contract_id = c.id
GROUP BY c.agent_id, c.id;

INSERT INTO "notifications" (user_id, team_id, title, message, is_read)
VALUES
((SELECT id FROM "users" WHERE email = 'alessia.riva@gestoreagenti.local'), (SELECT id FROM "teams" WHERE name = 'Vendite'), 'Monitoraggio dashboard', 'Nuove metriche di fatturato disponibili', 0),
((SELECT id FROM "users" WHERE email = 'marco.venturi@gestoreagenti.local'), (SELECT id FROM "teams" WHERE name = 'Vendite'), 'Pipeline Q3', 'Completa la revisione della pipeline Q3', 0),
((SELECT id FROM "users" WHERE email = 'luca.ferri@gestoreagenti.local'), (SELECT id FROM "teams" WHERE name = 'Vendite'), 'Pagamento registrato', 'Il cliente Helios Pharma ha saldato FA-2024-030', 1),
((SELECT id FROM "users" WHERE email = 'martina.conti@gestoreagenti.local'), (SELECT id FROM "teams" WHERE name = 'Vendite'), 'Fattura in attesa', 'FA-2024-025 è in attesa di pagamento', 0),
((SELECT id FROM "users" WHERE email = 'sergio.leoni@gestoreagenti.local'), (SELECT id FROM "teams" WHERE name = 'Enterprise'), 'Rinnovo canone', 'GreenPulse Energy rinnova al 15/09', 1),
((SELECT id FROM "users" WHERE email = 'giulia.bianchi@gestoreagenti.local'), (SELECT id FROM "teams" WHERE name = 'Partner Channel'), 'Canale partner', 'Disponibili materiali marketing aggiornati', 0),
((SELECT id FROM "users" WHERE email = 'paolo.serra@gestoreagenti.local'), (SELECT id FROM "teams" WHERE name = 'Enterprise'), 'Ticket critico', 'TechBridge chiede escalation prioritaria', 0),
((SELECT id FROM "users" WHERE email = 'elisa.romano@gestoreagenti.local'), (SELECT id FROM "teams" WHERE name = 'Partner Channel'), 'Contratto scaduto', 'Farmacia San Carlo ha chiuso il contratto 2023', 1),
((SELECT id FROM "users" WHERE email = 'luca.ferri@gestoreagenti.local'), (SELECT id FROM "teams" WHERE name = 'Vendite'), 'Chat team', 'Nuovo messaggio nel canale Vendite', 1),
((SELECT id FROM "users" WHERE email = 'martina.conti@gestoreagenti.local'), (SELECT id FROM "teams" WHERE name = 'Vendite'), 'Nuovo cliente', 'Ottica VisionPiu'' aggiunto al portafoglio', 0),
((SELECT id FROM "users" WHERE email = 'sergio.leoni@gestoreagenti.local'), (SELECT id FROM "teams" WHERE name = 'Enterprise'), 'Documenti aggiornati', 'Sono state caricate 3 nuove fatture', 1),
((SELECT id FROM "users" WHERE email = 'giulia.bianchi@gestoreagenti.local'), (SELECT id FROM "teams" WHERE name = 'Partner Channel'), 'Report mensile', 'Disponibile il ranking agenti partner', 0),
((SELECT id FROM "users" WHERE email = 'paolo.serra@gestoreagenti.local'), (SELECT id FROM "teams" WHERE name = 'Enterprise'), 'Pagamento in ritardo', 'FA-2024-022 non risulta saldata', 0),
((SELECT id FROM "users" WHERE email = 'elisa.romano@gestoreagenti.local'), (SELECT id FROM "teams" WHERE name = 'Partner Channel'), 'Aggiornamento indirizzi', 'Conferma gli indirizzi di spedizione', 1),
((SELECT id FROM "users" WHERE email = 'alessia.riva@gestoreagenti.local'), (SELECT id FROM "teams" WHERE name = 'Vendite'), 'Report trimestrale', 'Il report provvigioni Q2 è pronto', 0);

INSERT INTO "notification_subscriptions" (user_id, channel)
VALUES
((SELECT id FROM "users" WHERE email = 'luca.ferri@gestoreagenti.local'), 'desktop'),
((SELECT id FROM "users" WHERE email = 'martina.conti@gestoreagenti.local'), 'email'),
((SELECT id FROM "users" WHERE email = 'sergio.leoni@gestoreagenti.local'), 'desktop'),
((SELECT id FROM "users" WHERE email = 'giulia.bianchi@gestoreagenti.local'), 'mobile'),
((SELECT id FROM "users" WHERE email = 'paolo.serra@gestoreagenti.local'), 'mobile'),
((SELECT id FROM "users" WHERE email = 'elisa.romano@gestoreagenti.local'), 'email');

INSERT INTO "chat_rooms" (name, team_id)
VALUES
('Vendite - All Hands', (SELECT id FROM "teams" WHERE name = 'Vendite')),
('Enterprise - Deal Desk', (SELECT id FROM "teams" WHERE name = 'Enterprise')),
('Partner Channel', (SELECT id FROM "teams" WHERE name = 'Partner Channel'));

INSERT INTO "messages" (conversation_id, sender_id, team_id, body)
VALUES
('vendite-all', (SELECT id FROM "users" WHERE email = 'alessia.riva@gestoreagenti.local'), (SELECT id FROM "teams" WHERE name = 'Vendite'), 'Ricordate di aggiornare la pipeline entro venerdi'),
('vendite-all', (SELECT id FROM "users" WHERE email = 'luca.ferri@gestoreagenti.local'), (SELECT id FROM "teams" WHERE name = 'Vendite'), 'Helios Pharma ha confermato l''ordine di upgrade'),
('vendite-all', (SELECT id FROM "users" WHERE email = 'martina.conti@gestoreagenti.local'), (SELECT id FROM "teams" WHERE name = 'Vendite'), 'RetailUp ha richiesto una demo aggiuntiva'),
('enterprise-desk', (SELECT id FROM "users" WHERE email = 'sergio.leoni@gestoreagenti.local'), (SELECT id FROM "teams" WHERE name = 'Enterprise'), 'Ricorda il kick-off con GreenPulse il 12/09'),
('enterprise-desk', (SELECT id FROM "users" WHERE email = 'paolo.serra@gestoreagenti.local'), (SELECT id FROM "teams" WHERE name = 'Enterprise'), 'TechBridge chiede una call tecnica'),
('partner-room', (SELECT id FROM "users" WHERE email = 'giulia.bianchi@gestoreagenti.local'), (SELECT id FROM "teams" WHERE name = 'Partner Channel'), 'Disponibile la promo Q3 per i partner'),
('partner-room', (SELECT id FROM "users" WHERE email = 'elisa.romano@gestoreagenti.local'), (SELECT id FROM "teams" WHERE name = 'Partner Channel'), 'Ho aggiornato il listino per Farmacia San Carlo'),
('vendite-all', (SELECT id FROM "users" WHERE email = 'marco.venturi@gestoreagenti.local'), (SELECT id FROM "teams" WHERE name = 'Vendite'), 'Ottimo lavoro sul Q2, continuiamo cosi');

CREATE TABLE IF NOT EXISTS "statistics_yearly" (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    reference_year INT NOT NULL,
    total_revenue DECIMAL(19, 2) NOT NULL,
    total_commissions DECIMAL(19, 2) NOT NULL,
    managed_customers INT NOT NULL,
    top_agent_id BIGINT,
    CONSTRAINT fk_statistics_yearly_agent FOREIGN KEY (top_agent_id) REFERENCES "agents"(id)
);

CREATE TABLE IF NOT EXISTS "statistics_monthly" (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    reference_year INT NOT NULL,
    reference_month INT NOT NULL,
    revenue DECIMAL(19, 2) NOT NULL,
    commissions DECIMAL(19, 2) NOT NULL,
    active_customers INT NOT NULL,
    top_agent_id BIGINT,
    CONSTRAINT fk_statistics_monthly_agent FOREIGN KEY (top_agent_id) REFERENCES "agents"(id)
);

INSERT INTO "statistics_yearly" (reference_year, total_revenue, total_commissions, managed_customers, top_agent_id)
VALUES
(2024, (SELECT COALESCE(SUM(amount),0) FROM "invoices" WHERE status = 'PAID'),
       (SELECT COALESCE(SUM(amount * 0.08),0) FROM "invoices" WHERE status = 'PAID'),
       (SELECT COUNT(*) FROM "customers"),
       (SELECT TOP 1 c.agent_id FROM "contracts" c JOIN "invoices" i ON i.contract_id = c.id WHERE i.status = 'PAID' GROUP BY c.agent_id ORDER BY SUM(i.amount) DESC));

WITH invoices_with_month AS (
    SELECT id,
           contract_id,
           customer_id,
           status,
           amount,
           issue_date,
           EXTRACT(MONTH FROM issue_date) AS month_no
    FROM "invoices"
), monthly_base AS (
    SELECT month_no,
           SUM(CASE WHEN status = 'PAID' THEN amount ELSE 0 END) AS revenue,
           SUM(CASE WHEN status = 'PAID' THEN amount * 0.08 ELSE 0 END) AS commissions,
           COUNT(DISTINCT customer_id) AS active_customers
    FROM invoices_with_month
    GROUP BY month_no
), monthly_top_agents AS (
    SELECT month_no, agent_id
    FROM (
        SELECT mt.month_no,
               mt.agent_id,
               ROW_NUMBER() OVER (
                   PARTITION BY mt.month_no
                   ORDER BY mt.paid_amount DESC, mt.agent_id
               ) AS rn
        FROM (
            SELECT iw.month_no,
                   c.agent_id,
                   SUM(iw.amount) AS paid_amount
            FROM invoices_with_month iw
            JOIN "contracts" c ON iw.contract_id = c.id
            WHERE iw.status = 'PAID'
            GROUP BY iw.month_no, c.agent_id
        ) mt
    ) ranked
    WHERE rn = 1
)
INSERT INTO "statistics_monthly" (reference_year, reference_month, revenue, commissions, active_customers, top_agent_id)
SELECT 2024,
       mb.month_no,
       mb.revenue,
       mb.commissions,
       mb.active_customers,
       mta.agent_id
FROM monthly_base mb
LEFT JOIN monthly_top_agents mta ON mta.month_no = mb.month_no
ORDER BY mb.month_no;
