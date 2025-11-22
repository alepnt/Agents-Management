IF OBJECT_ID('dbo.roles', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.roles (
        id BIGINT IDENTITY PRIMARY KEY,
        name NVARCHAR(100) NOT NULL UNIQUE
    );
END;

IF OBJECT_ID('dbo.teams', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.teams (
        id BIGINT IDENTITY PRIMARY KEY,
        name NVARCHAR(100) NOT NULL UNIQUE
    );
END;

IF OBJECT_ID('dbo.users', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.users (
        id BIGINT IDENTITY PRIMARY KEY,
        azure_id NVARCHAR(100) NOT NULL UNIQUE,
        email NVARCHAR(255) NOT NULL,
        display_name NVARCHAR(255) NOT NULL,
        password_hash NVARCHAR(255),
        role_id BIGINT NOT NULL,
        team_id BIGINT NOT NULL,
        active BIT NOT NULL DEFAULT 1,
        created_at DATETIME2 NOT NULL DEFAULT ${utc_datetime_function},
        CONSTRAINT fk_users_roles FOREIGN KEY (role_id) REFERENCES dbo.roles (id),
        CONSTRAINT fk_users_teams FOREIGN KEY (team_id) REFERENCES dbo.teams (id)
    );
END;

IF OBJECT_ID('dbo.agents', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.agents (
        id BIGINT IDENTITY PRIMARY KEY,
        user_id BIGINT NOT NULL,
        agent_code NVARCHAR(64) NOT NULL,
        team_role NVARCHAR(128),
        CONSTRAINT fk_agents_users FOREIGN KEY (user_id) REFERENCES dbo.users (id),
        CONSTRAINT uq_agents_code UNIQUE (agent_code)
    );
END;

IF NOT EXISTS (SELECT 1 FROM dbo.roles WHERE name = 'Agent')
BEGIN
    INSERT INTO dbo.roles (name) VALUES ('Agent');
END;

IF NOT EXISTS (SELECT 1 FROM dbo.teams WHERE name = 'Vendite')
BEGIN
    INSERT INTO dbo.teams (name) VALUES ('Vendite');
END;
