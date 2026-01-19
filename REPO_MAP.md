# Repository Map (AI-optimized)

## Top-level
- **build.gradle.kts** — Gradle build (Kotlin JVM + Spring Boot 3.2.5, Java 17, Flyway, JPA, JDBC, PostgreSQL).
- **settings.gradle.kts** — Gradle settings.
- **compose.yaml** — Local dev services (not inspected).
- **openapi.yaml** — API spec (not inspected).
- **src/main/** — Application code/resources.

## Application entrypoint
- **src/main/kotlin/com/example/facilities/FacilitiesApplication.kt**
  Spring Boot bootstrap (`@SpringBootApplication`, `main`).

## Configuration
- **src/main/kotlin/com/example/facilities/config/CorsConfig.kt**
  CORS settings scoped to `/v1/**`, origins from `app.cors.allowed-origins`.
- **src/main/resources/application.yml**
  Datasource (Postgres), Jackson timezone, Flyway migrations, CORS env config.

## API layer (controllers + DTOs)
- **src/main/kotlin/com/example/facilities/api/controller/**
  - `CityController.kt` — `GET /v1/cities` → list cities.
  - `FacilityController.kt` — `GET /v1/facilities` (filters + paging) and `GET /v1/facilities/{facilityId}`.
  - `MetaController.kt` — `GET /v1/meta` for lookup metadata.
- **src/main/kotlin/com/example/facilities/api/dto/DtoModels.kt**
  DTOs for cities, facilities, capabilities, features, equipment, spaces, pricing, etc. Includes list/detail response shapes.

## Domain services
- **src/main/kotlin/com/example/facilities/domain/service/**
  - `CityService.kt` — maps `CityRepository` rows to `CityDTO`.
  - `FacilityService.kt` — main orchestration: filters, pagination, detail assembly (gallery, contacts, opening hours, spaces, equipment, features).
  - `MetaService.kt` — metadata lookup (capabilities, features, equipment categories, space types).

## Data access (repositories)
- **src/main/kotlin/com/example/facilities/domain/repo/**
  - `CityRepository.kt` — simple city lookup.
  - `FacilityRepository.kt` — complex SQL for filtering, pagination, and detail retrieval. Uses `NamedParameterJdbcTemplate`.
  - `MetaRepository.kt` — metadata lookup tables.

## Database migrations
- **src/main/resources/db/migration/**
  - `V1__init.sql` — base schema.
  - `V2__reference_data.sql` — reference tables.
  - `V3__seed_dev.sql` — development seed data.

## Request flow (mental model)
1. **HTTP → Controller** (`/v1/...`).
2. **Controller → Service** (input normalization, defaults).
3. **Service → Repository** (SQL via JDBC template).
4. **Repository → DTO mapping** in service → API response.
