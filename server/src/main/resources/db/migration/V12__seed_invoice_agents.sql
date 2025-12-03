-- Inserisce agenti aggiuntivi per la creazione di fatture demo

INSERT INTO "users" (azure_id, email, display_name, password_hash, role_id, team_id, active)
VALUES
('az-ag-007', 'ilaria.rossi@gestoreagenti.local', 'Ilaria Rossi', '$2a$10$O30tgOlPZ7UfAq5Jovg1JeNw2N5YI6TcEF/8ZbhvACyMfmB34lTgS',
 (SELECT id FROM "roles" WHERE name = 'Agent'), (SELECT id FROM "teams" WHERE name = 'Vendite'), 1),
('az-ag-008', 'davide.greco@gestoreagenti.local', 'Davide Greco', '$2a$10$O30tgOlPZ7UfAq5Jovg1JeNw2N5YI6TcEF/8ZbhvACyMfmB34lTgS',
 (SELECT id FROM "roles" WHERE name = 'Agent'), (SELECT id FROM "teams" WHERE name = 'Enterprise'), 1),
('az-ag-009', 'chiara.pagliani@gestoreagenti.local', 'Chiara Pagliani', '$2a$10$O30tgOlPZ7UfAq5Jovg1JeNw2N5YI6TcEF/8ZbhvACyMfmB34lTgS',
 (SELECT id FROM "roles" WHERE name = 'Agent'), (SELECT id FROM "teams" WHERE name = 'Partner Channel'), 1),
('az-ag-010', 'riccardo.moretti@gestoreagenti.local', 'Riccardo Moretti', '$2a$10$O30tgOlPZ7UfAq5Jovg1JeNw2N5YI6TcEF/8ZbhvACyMfmB34lTgS',
 (SELECT id FROM "roles" WHERE name = 'Agent'), (SELECT id FROM "teams" WHERE name = 'Vendite'), 1),
('az-ag-011', 'alessandra.costa@gestoreagenti.local', 'Alessandra Costa', '$2a$10$O30tgOlPZ7UfAq5Jovg1JeNw2N5YI6TcEF/8ZbhvACyMfmB34lTgS',
 (SELECT id FROM "roles" WHERE name = 'Agent'), (SELECT id FROM "teams" WHERE name = 'Enterprise'), 1)
ON CONFLICT (email) DO NOTHING;

INSERT INTO "agents" (user_id, agent_code, team_role)
SELECT u.id, 'AG007', 'Billing Specialist'
FROM "users" u WHERE u.email = 'ilaria.rossi@gestoreagenti.local'
    AND NOT EXISTS (SELECT 1 FROM "agents" a WHERE a.agent_code = 'AG007');
INSERT INTO "agents" (user_id, agent_code, team_role)
SELECT u.id, 'AG008', 'Invoice Manager'
FROM "users" u WHERE u.email = 'davide.greco@gestoreagenti.local'
    AND NOT EXISTS (SELECT 1 FROM "agents" a WHERE a.agent_code = 'AG008');
INSERT INTO "agents" (user_id, agent_code, team_role)
SELECT u.id, 'AG009', 'Revenue Specialist'
FROM "users" u WHERE u.email = 'chiara.pagliani@gestoreagenti.local'
    AND NOT EXISTS (SELECT 1 FROM "agents" a WHERE a.agent_code = 'AG009');
INSERT INTO "agents" (user_id, agent_code, team_role)
SELECT u.id, 'AG010', 'Collections Advisor'
FROM "users" u WHERE u.email = 'riccardo.moretti@gestoreagenti.local'
    AND NOT EXISTS (SELECT 1 FROM "agents" a WHERE a.agent_code = 'AG010');
INSERT INTO "agents" (user_id, agent_code, team_role)
SELECT u.id, 'AG011', 'Senior Biller'
FROM "users" u WHERE u.email = 'alessandra.costa@gestoreagenti.local'
    AND NOT EXISTS (SELECT 1 FROM "agents" a WHERE a.agent_code = 'AG011');
