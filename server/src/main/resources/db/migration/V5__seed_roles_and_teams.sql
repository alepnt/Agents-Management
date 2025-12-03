INSERT INTO "roles" ("name")
VALUES ('Amministratore')
ON CONFLICT ("name") DO NOTHING;

INSERT INTO "roles" ("name")
VALUES ('Manager')
ON CONFLICT ("name") DO NOTHING;

INSERT INTO "teams" ("name")
VALUES ('Supporto')
ON CONFLICT ("name") DO NOTHING;

INSERT INTO "teams" ("name")
VALUES ('Marketing')
ON CONFLICT ("name") DO NOTHING;
