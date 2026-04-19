# Deployment Tracker

A Release & Deployment Tracking app built with Vaadin 24, Spring Boot 3, and PostgreSQL.
Used as the learning vehicle for a full CI/CD pipeline learning path with Jenkins.

## Stack

| Layer    | Technology              |
|----------|-------------------------|
| UI       | Vaadin 24               |
| Backend  | Spring Boot 3           |
| Database | PostgreSQL              |
| ORM      | Spring Data JPA         |
| Build    | Maven                   |
| Tests    | JUnit 5 (H2 in-memory) |

## Running Locally

### Prerequisites
- Java 17+
- Maven 3.9+
- PostgreSQL running locally

### Database Setup
```sql
CREATE DATABASE devtracker;
CREATE USER devtracker WITH PASSWORD 'devtracker';
GRANT ALL PRIVILEGES ON DATABASE devtracker TO devtracker;
```

### Run
```bash
mvn spring-boot:run
```
App starts at http://localhost:8080

### Run Tests (no DB required)
```bash
mvn test
```

## Project Structure

```
src/
├── main/java/com/devtracker/
│   ├── Application.java         # Entry point
│   ├── models/                  # JPA entities
│   │   ├── Project.java
│   │   └── Deployment.java
│   ├── repositories/            # Spring Data repositories
│   ├── services/                # Business logic
│   └── views/                   # Vaadin UI views
│       ├── MainLayout.java      # Sidebar nav
│       ├── DashboardView.java   # Stats overview
│       ├── ProjectsView.java    # CRUD for projects
│       └── DeploymentsView.java # Log & manage deployments
└── test/                        # JUnit tests (uses H2)
```

## Environment Variables

| Variable  | Description           | Default                              |
|-----------|-----------------------|--------------------------------------|
| `DB_URL`  | JDBC connection URL   | jdbc:postgresql://localhost:5432/devtracker |
| `DB_USER` | Database username     | devtracker                           |
| `DB_PASS` | Database password     | devtracker                           |