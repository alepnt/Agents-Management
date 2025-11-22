IF OBJECT_ID('dbo.contracts', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.contracts (
        id BIGINT IDENTITY PRIMARY KEY,
        agent_id BIGINT NOT NULL,
        customer_name NVARCHAR(255) NOT NULL,
        description NVARCHAR(1000),
        start_date DATE NOT NULL,
        end_date DATE,
        total_value DECIMAL(19, 2) NOT NULL,
        status NVARCHAR(50) NOT NULL,
        CONSTRAINT fk_contracts_agents FOREIGN KEY (agent_id) REFERENCES dbo.agents (id)
    );
END;

IF OBJECT_ID('dbo.invoices', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.invoices (
        id BIGINT IDENTITY PRIMARY KEY,
        contract_id BIGINT,
        invoice_number NVARCHAR(100) NOT NULL,
        customer_name NVARCHAR(255) NOT NULL,
        amount DECIMAL(19, 2) NOT NULL,
        issue_date DATE NOT NULL,
        due_date DATE,
        status NVARCHAR(50) NOT NULL,
        payment_date DATE,
        notes NVARCHAR(1000),
        created_at DATETIME2 NOT NULL DEFAULT ${utc_datetime_function},
        updated_at DATETIME2,
        CONSTRAINT fk_invoices_contract FOREIGN KEY (contract_id) REFERENCES dbo.contracts (id)
    );
END;

IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'uq_invoices_number'
      AND object_id = OBJECT_ID('dbo.invoices')
)
BEGIN
    CREATE UNIQUE INDEX uq_invoices_number ON dbo.invoices (invoice_number);
END;

IF OBJECT_ID('dbo.document_history', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.document_history (
        id BIGINT IDENTITY PRIMARY KEY,
        document_type NVARCHAR(50) NOT NULL,
        document_id BIGINT NOT NULL,
        action NVARCHAR(50) NOT NULL,
        description NVARCHAR(1000),
        created_at DATETIME2 NOT NULL DEFAULT ${utc_datetime_function}
    );
END;

IF OBJECT_ID('dbo.commissions', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.commissions (
        id BIGINT IDENTITY PRIMARY KEY,
        agent_id BIGINT NOT NULL,
        contract_id BIGINT NOT NULL,
        total_commission DECIMAL(19, 2) NOT NULL,
        paid_commission DECIMAL(19, 2) NOT NULL,
        pending_commission DECIMAL(19, 2) NOT NULL,
        last_updated DATETIME2 NOT NULL,
        CONSTRAINT fk_commission_contract FOREIGN KEY (contract_id) REFERENCES dbo.contracts (id),
        CONSTRAINT fk_commission_agent FOREIGN KEY (agent_id) REFERENCES dbo.agents (id),
        CONSTRAINT uq_commission_agent_contract UNIQUE (agent_id, contract_id)
    );
END;
