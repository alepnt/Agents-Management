MERGE INTO "roles" ("name") KEY("name") VALUES ('Agent');
MERGE INTO "teams" ("name") KEY("name") VALUES ('Vendite');

MERGE INTO "users" ("azure_id", "email", "display_name", "password_hash", "role_id", "team_id") KEY("email")
VALUES (
    'dev-user',
    'dev.user@example.com',
    'Dev User',
    '$2a$10$uKqYcu4B0XlBWxY3Q5BjxOxOpoZ1ZT/ihJIQx7zR7pVICdjbFtoIa',
    (SELECT "id" FROM "roles" WHERE "name" = 'Agent'),
    (SELECT "id" FROM "teams" WHERE "name" = 'Vendite')
);
