-- Seed a local agent code and password for the development user
-- Hash generated with SHA-256 of the string "password1"
UPDATE users
SET password_hash = '0b14d501a594442a01c6859541bcb3e8164d183d32937b851835442f69d5c94e'
WHERE email = 'dev.user@example.com';

MERGE INTO agents (user_id, agent_code, team_role) KEY(agent_code)
VALUES (
    (SELECT id FROM users WHERE email = 'dev.user@example.com'),
    'DEV001',
    'Developer'
);
