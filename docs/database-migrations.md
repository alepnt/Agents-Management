# Database migrations

Flyway applies migrations in version order. The current scripts are numbered sequentially to avoid conflicts:

| Version | File | Purpose |
|---------|------|---------|
| V1 | `V1__create_identity_tables.sql` | Identity/authentication tables |
| V2 | `V2__create_documents_tables.sql` | Document domain tables |
| V3 | `V3__notifications_and_chat.sql` | Notifications and chat tables |
| V4 | `V4__create_customer_article_tables.sql` | Customer and article tables |
| V5 | `V5__seed_roles_and_teams.sql` | Initial roles and team data |

If you add a new migration, pick the next available version number to prevent the "Found more than one migration with version" error during startup.
