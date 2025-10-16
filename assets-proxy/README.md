# Assets Proxy – Technical README

## 1. Overview
This project provides a RESTful service for managing asset uploads and metadata storage. It acts as a proxy between internal systems and an external publisher, allowing asynchronous upload management, asset searching, and persistence of related data.

The system is designed following **Hexagonal Architecture** and **Domain-Driven Design (DDD)** principles, with strict adherence to **SOLID** principles to ensure maintainability, scalability, and testability.

### Business Context
The business requirement is to offer an asset upload feature that supports:
1. File upload management (receiving the file and delegating the upload asynchronously to an internal publisher).
2. Asset search functionality based on persisted metadata.

This service does **not** act as a final storage destination; it functions as a proxy, maintaining asset information and mediating upload processes.

---

## 2. Architecture and Design
The project follows a **Hexagonal Architecture (Ports and Adapters)** pattern:
- **Domain layer**: Contains business logic, domain models, and value objects.
- **Application layer**: Coordinates use cases, interacts with domain logic, and defines ports for external dependencies.
- **Infrastructure layer**: Implements adapters such as REST controllers, JPA repositories, mappers, and external integrations.

The structure aligns with **DDD** principles:
- Clear separation of concerns between domain, application, and infrastructure.
- Domain models are persistent-agnostic and independent from frameworks.
- Application services orchestrate domain actions and persistence through interfaces.

**SOLID** principles are consistently applied:
- **Single Responsibility**: Each component has a clear, isolated responsibility.
- **Open/Closed**: Use of abstractions for extension without modification.
- **Liskov Substitution**: Consistent interface contracts.
- **Interface Segregation**: Fine-grained interfaces between layers.
- **Dependency Inversion**: The domain defines interfaces; infrastructure implements them.

---

## 3. Technical Stack
- **Java 21**
- **Spring Boot 3.5.6**
- **Spring Data JPA** (PostgreSQL persistence)
- **Spring Validation**
- **MapStruct** for DTO mapping
- **Lombok** for boilerplate reduction
- **Actuator** for monitoring
- **JUnit & Spring Boot Test** for unit and integration tests

Build tool: **Maven**

---

## 4. Database Schema
The persistence layer uses PostgreSQL. The database schema is initialized via `schema.sql`.

**Table: assets**
```sql
CREATE TABLE assets (
  id SERIAL PRIMARY KEY,
  filename VARCHAR,
  content_type VARCHAR,
  size BIGINT,
  url VARCHAR,
  upload_date TIMESTAMPTZ NOT NULL DEFAULT now(),
  status VARCHAR NOT NULL DEFAULT 'PENDING'
    CHECK (status IN ('PENDING','UPLOADING','COMPLETED','FAILED'))
);
```
Indexes support optimized searches by filename, upload date, and status.

---

## 5. API Overview
The REST API definition is provided in `openapi.yml`. It exposes endpoints for:
- **POST /api/mgmt/{version}/assets/actions/upload** – Uploads an asset asynchronously.
- **GET /api/mgmt/{version}/assets** – Retrieves assets by filters (filename, date range, status) with sorting.

The **GET** endpoint supports the following optional filters:
- `uploadDateStart`: start date for the search range.
- `uploadDateEnd`: end date for the search range.
- `sortDirection`: sorting direction (ASC or DESC). Default DESC.
- `filename`: name of the file.
- `filetype`: MIME type of the file.

**Example usage:**
```bash
GET http://localhost:8080/api/mgmt/1/assets?uploadDateStart=2025-01-15T17:00:59Z&uploadDateEnd=2025-10-16T17:00:59Z&sortDirection=ASC&filename=logo_empresa2.png&filetype=image/png
```

A **Postman Collection** (`ASSET.postman_collection.json`) is included for testing different query and upload scenarios.

---

## 6. Run Instructions

### Local execution
```bash
mvn clean package -DskipTests
mvn spring-boot:run
```
The application will start on the default port `8080`.

### Docker execution
```bash
docker-compose up --build -d
```
When running in Docker, the service port may change depending on the configuration.  
If using the provided Postman collection, update the URLs accordingly to match the exposed container port.

**Note:**  
- Use port `8080` when running locally via IDE.  
- Use the dynamically mapped port from Docker when running in containerized mode.

---

## 7. Testing
The project includes both **unit** and **integration tests** using JUnit and Spring Boot Test.  

Tests cover domain logic, persistence layer, and REST endpoints.

Test coverage is verified using JaCoCo, achieving approximately 95% coverage across domain logic, application services, and REST controllers.

Tests can be executed with:
```bash
mvn test
```

---

## 8. References
- API specification: `openapi.yml`
- Database schema: `schema.sql`
- Postman collection: `ASSET.postman_collection.json`
- Docker configuration: `Dockerfile`, `docker-compose.yml`
- Application configuration: `application.yml`

---

## 9. Quick Commands Reference
| Action | Command |
|--------|----------|
| Build without tests | `mvn clean package -DskipTests` |
| Run locally | `mvn spring-boot:run` |
| Run with Docker | `docker-compose up --build -d` |
| Run tests | `mvn test` |

---



