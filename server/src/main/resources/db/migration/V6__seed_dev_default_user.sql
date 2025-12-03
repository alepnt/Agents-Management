-- Seed a default developer user to simplify local logins without registration
-- The credentials are intentionally static and should only be shared among developers.

-- Ensure default role and team exist
INSERT INTO "roles" ("name") VALUES ('Agent') ON CONFLICT ("name") DO NOTHING;
INSERT INTO "teams" ("name") VALUES ('Vendite') ON CONFLICT ("name") DO NOTHING;

-- Create the developer user if missing
INSERT INTO "users" ("azure_id", "email", "display_name", "password_hash", "role_id", "team_id", "active", "created_at")
SELECT 'dev-local-azure', 'dev@example.com', 'Developer Account', 'f1b75f175f7c59db18926e65cf08b4788376a6ab77316f3b96a99920303dc4a6',
       (SELECT "id" FROM "roles" WHERE "name" = 'Agent'),
       (SELECT "id" FROM "teams" WHERE "name" = 'Vendite'),
       1, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM "users" WHERE "azure_id" = 'dev-local-azure');

-- Associate a predictable agent code for local login
INSERT INTO "agents" ("user_id", "agent_code", "team_role")
SELECT "id", 'DEV-001', 'Developer'
FROM "users"
WHERE "azure_id" = 'dev-local-azure'
  AND NOT EXISTS (SELECT 1 FROM "agents" WHERE "agent_code" = 'DEV-001');
