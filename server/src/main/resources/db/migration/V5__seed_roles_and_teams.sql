INSERT INTO "roles" ("NAME")
SELECT 'Amministratore'
WHERE NOT EXISTS (SELECT 1 FROM "roles" WHERE "NAME" = 'Amministratore');

INSERT INTO "roles" ("NAME")
SELECT 'Responsabile Team'
WHERE NOT EXISTS (SELECT 1 FROM "roles" WHERE "NAME" = 'Responsabile Team');

INSERT INTO "roles" ("NAME")
SELECT 'Back Office'
WHERE NOT EXISTS (SELECT 1 FROM "roles" WHERE "NAME" = 'Back Office');

INSERT INTO "teams" ("NAME")
SELECT 'Vendite Nord'
WHERE NOT EXISTS (SELECT 1 FROM "teams" WHERE "NAME" = 'Vendite Nord');

INSERT INTO "teams" ("NAME")
SELECT 'Vendite Sud'
WHERE NOT EXISTS (SELECT 1 FROM "teams" WHERE "NAME" = 'Vendite Sud');

INSERT INTO "teams" ("NAME")
SELECT 'Marketing'
WHERE NOT EXISTS (SELECT 1 FROM "teams" WHERE "NAME" = 'Marketing');

INSERT INTO "teams" ("NAME")
SELECT 'Supporto Clienti'
WHERE NOT EXISTS (SELECT 1 FROM "teams" WHERE "NAME" = 'Supporto Clienti');
