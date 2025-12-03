-- Aggiunge clienti e contratti aggiuntivi per arricchire i dati demo

INSERT INTO "customers" (name, vat_number, tax_code, email, phone, address)
SELECT 'Alfa Automotive', 'IT20112233445', 'ALFA88990A', 'acquisti@alfaauto.it', '+39-011-778899', 'Via Industria 14, Torino'
WHERE NOT EXISTS (SELECT 1 FROM "customers" WHERE name = 'Alfa Automotive');

INSERT INTO "customers" (name, vat_number, tax_code, email, phone, address)
SELECT 'Mediterraneo Foods', 'IT22113344556', 'MDFS77110B', 'ordini@mediterraneofoods.it', '+39-080-998877', 'Via Bari 22, Bari'
WHERE NOT EXISTS (SELECT 1 FROM "customers" WHERE name = 'Mediterraneo Foods');

INSERT INTO "customers" (name, vat_number, tax_code, email, phone, address)
SELECT 'Skyline Hotels', 'IT23114455667', 'SKLN88221C', 'procurement@skylinehotels.com', '+39-070-776655', 'Viale del Mare 8, Cagliari'
WHERE NOT EXISTS (SELECT 1 FROM "customers" WHERE name = 'Skyline Hotels');

INSERT INTO "customers" (name, vat_number, tax_code, email, phone, address)
SELECT 'Orizzonte Tech', 'IT24115566778', 'ORIZ99332D', 'finance@orizzontetech.it', '+39-02-6677889', 'Via Sforza 19, Milano'
WHERE NOT EXISTS (SELECT 1 FROM "customers" WHERE name = 'Orizzonte Tech');

INSERT INTO "customers" (name, vat_number, tax_code, email, phone, address)
SELECT 'Biotech Labs', 'IT25116677889', 'BTLB00443E', 'pagamenti@biotechlabs.eu', '+39-049-556677', 'Via Galileo 4, Padova'
WHERE NOT EXISTS (SELECT 1 FROM "customers" WHERE name = 'Biotech Labs');

INSERT INTO "contracts" (agent_id, customer_name, description, start_date, end_date, total_value, status)
SELECT (SELECT id FROM "agents" WHERE agent_code = 'AG001'), 'Alfa Automotive', 'Piattaforma di telemetria flotte', '2024-04-01', '2026-03-31', 410000.00, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM "contracts" WHERE customer_name = 'Alfa Automotive' AND description = 'Piattaforma di telemetria flotte');

INSERT INTO "contracts" (agent_id, customer_name, description, start_date, end_date, total_value, status)
SELECT (SELECT id FROM "agents" WHERE agent_code = 'AG002'), 'Mediterraneo Foods', 'Programma loyalty e mobile app', '2024-06-10', NULL, 185000.00, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM "contracts" WHERE customer_name = 'Mediterraneo Foods' AND description = 'Programma loyalty e mobile app');

INSERT INTO "contracts" (agent_id, customer_name, description, start_date, end_date, total_value, status)
SELECT (SELECT id FROM "agents" WHERE agent_code = 'AG003'), 'Skyline Hotels', 'Suite CRM per catena alberghiera', '2023-11-20', NULL, 295000.00, 'SUSPENDED'
WHERE NOT EXISTS (SELECT 1 FROM "contracts" WHERE customer_name = 'Skyline Hotels' AND description = 'Suite CRM per catena alberghiera');

INSERT INTO "contracts" (agent_id, customer_name, description, start_date, end_date, total_value, status)
SELECT (SELECT id FROM "agents" WHERE agent_code = 'AG004'), 'Orizzonte Tech', 'Portale partner e onboarding reseller', '2024-02-15', '2025-02-14', 230000.00, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM "contracts" WHERE customer_name = 'Orizzonte Tech' AND description = 'Portale partner e onboarding reseller');

INSERT INTO "contracts" (agent_id, customer_name, description, start_date, end_date, total_value, status)
SELECT (SELECT id FROM "agents" WHERE agent_code = 'AG005'), 'Biotech Labs', 'Licenze laboratorio e supporto 24/7', '2022-09-01', '2024-08-31', 315000.00, 'TERMINATED'
WHERE NOT EXISTS (SELECT 1 FROM "contracts" WHERE customer_name = 'Biotech Labs' AND description = 'Licenze laboratorio e supporto 24/7');

INSERT INTO "commissions" (agent_id, contract_id, total_commission, paid_commission, pending_commission, last_updated)
SELECT c.agent_id, c.id, 0, 0, 0, CURRENT_TIMESTAMP
FROM "contracts" c
WHERE c.customer_name IN ('Alfa Automotive', 'Mediterraneo Foods', 'Skyline Hotels', 'Orizzonte Tech', 'Biotech Labs')
  AND NOT EXISTS (
    SELECT 1
    FROM "commissions" cm
    WHERE cm.contract_id = c.id AND cm.agent_id = c.agent_id
);
