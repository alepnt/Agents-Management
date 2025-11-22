IF OBJECT_ID('dbo.notifications', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.notifications (
        id BIGINT IDENTITY PRIMARY KEY,
        user_id BIGINT NULL,
        team_id BIGINT NULL,
        title NVARCHAR(255) NOT NULL,
        message NVARCHAR(MAX) NOT NULL,
        is_read BIT NOT NULL DEFAULT 0,
        created_at DATETIME2 NOT NULL DEFAULT ${utc_datetime_function}
    );
END;
GO

IF OBJECT_ID('dbo.notification_subscriptions', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.notification_subscriptions (
        id BIGINT IDENTITY PRIMARY KEY,
        user_id BIGINT NOT NULL,
        channel NVARCHAR(128) NOT NULL,
        created_at DATETIME2 NOT NULL DEFAULT ${utc_datetime_function}
    );
END;
GO

IF OBJECT_ID('dbo.messages', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.messages (
        id BIGINT IDENTITY PRIMARY KEY,
        conversation_id NVARCHAR(128) NOT NULL,
        sender_id BIGINT NOT NULL,
        team_id BIGINT NULL,
        body NVARCHAR(MAX) NOT NULL,
        created_at DATETIME2 NOT NULL DEFAULT ${utc_datetime_function}
    );
END;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'idx_notifications_user'
      AND object_id = OBJECT_ID('dbo.notifications')
)
BEGIN
    CREATE INDEX idx_notifications_user ON dbo.notifications (user_id, created_at DESC);
END;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'idx_notifications_team'
      AND object_id = OBJECT_ID('dbo.notifications')
)
BEGIN
    CREATE INDEX idx_notifications_team ON dbo.notifications (team_id, created_at DESC);
END;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'idx_messages_conversation'
      AND object_id = OBJECT_ID('dbo.messages')
)
BEGIN
    CREATE INDEX idx_messages_conversation ON dbo.messages (conversation_id, created_at);
END;
GO
