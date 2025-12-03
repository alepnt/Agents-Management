MERGE INTO "roles" ("name") KEY("name")
VALUES ('Amministratore');

MERGE INTO "roles" ("name") KEY("name")
VALUES ('Manager');

MERGE INTO "teams" ("name") KEY("name")
VALUES ('Supporto');

MERGE INTO "teams" ("name") KEY("name")
VALUES ('Marketing');
