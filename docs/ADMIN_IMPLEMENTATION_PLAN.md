# Admin API Implementation Plan

This plan tracks the implementation of the Admin Panel API (full CRUD) that complements the existing public read-only API. It is structured to be followed step-by-step and can be checked off as work lands.

## 0) Repository orientation

- Public API code lives under `src/main/kotlin/com/example/facilities/api/controller` and is currently read-only.
- Domain services and repositories live under `src/main/kotlin/com/example/facilities/domain`.
- Flyway migrations live under `src/main/resources/db/migration`.

## 1) Security & config (HTTP Basic for `/admin/v1/**`)

- [ ] Add `config/SecurityConfig.kt`.
  - Protect `/admin/v1/**` with HTTP Basic authentication.
  - Permit all for `/v1/**`.
  - Load credentials from `ADMIN_USERNAME` / `ADMIN_PASSWORD` env vars.
  - Use `SecurityFilterChain`, `UserDetailsService`, `PasswordEncoder` (e.g., BCrypt).
- [ ] Confirm CORS behavior for admin endpoints.
  - If admin UI is browser-based, allow the admin UI origin for `/admin/**` as needed.

## 2) DTOs & validation models

- [ ] Create admin DTOs aligned with the OpenAPI schemas.
  - Examples: `AdminCity`, `AdminCityCreate`, `AdminCityUpdate`, `AdminFacility`, `AdminFacilityCreate`, `AdminFacilityUpdate`, `AdminMedia`, `AdminMediaCreate`, etc.
- [ ] Add validation annotations where applicable.
  - Required fields: `@NotBlank`, `@NotNull`.
  - URL validation for media: `@Pattern` or `@URL` with `http(s)` scheme.

## 3) Persistence layer (admin CRUD)

Choose one of the following approaches (both are supported in the repo):

### Option A: JPA for admin CRUD (recommended)

- [ ] Add entity models under `domain/entity` for:
  - `City`, `Address`, `Facility`, `Media`, `FacilityMedia`, `ContactPoint`, `CapabilityType`, `FacilityCapability`, `Feature`, `FacilityFeature`, `SpaceType`, `Space`, `SpaceMedia`, `EquipmentType`, `FacilityEquipment`, `OpeningHours`, `PriceInfo`.
- [ ] Add `JpaRepository` interfaces under `domain/repo`.

### Option B: JDBC for admin CRUD (consistent with public side)

- [ ] Add `Admin*Repository` classes using `NamedParameterJdbcTemplate`.
- [ ] Implement CRUD SQL and replace-all operations as delete-then-insert within a transaction.

## 4) Services (validation + orchestration)

- [ ] Implement admin services (e.g., `AdminCityService`, `AdminFacilityService`, `AdminMediaService`, `AdminReferenceService`, `AdminSpaceService`, `AdminAddressService`).
- [ ] Enforce validation rules:
  - Facility **must** have `cityId`.
  - If `addressId != null`, verify `address.cityId == facility.cityId` (keep DB trigger as the final guard).
  - `media.url` must be `http(s)` URL.
- [ ] Enforce deletion constraints:
  - Reference data (`capability_type`, `feature`, `equipment_type`, `space_type`) must return **409** if referenced.
  - Facility deletion should cascade (already defined in DB schema).

## 5) Controllers (admin endpoints)

Create controllers under `api/admin` to match the OpenAPI spec:

- [ ] `AdminCitiesController` for `/admin/v1/cities`.
- [ ] `AdminFacilitiesController` for `/admin/v1/facilities` + subresources:
  - `/contacts`, `/opening-hours`, `/capabilities`, `/features`, `/equipment`, `/prices`, `/media` (replace-all PUT).
- [ ] `AdminSpacesController` for `/admin/v1/spaces` + `/media`.
- [ ] `AdminMediaController` for `/admin/v1/media`.
- [ ] `AdminReferenceController` for:
  - `/reference/capability-types`
  - `/reference/features`
  - `/reference/space-types`
  - `/reference/equipment-types`

## 6) OpenAPI alignment

- [ ] Ensure `openapi.yaml` matches the finalized spec.
- [ ] Update schema/endpoint docs if names or paths differ.

## 7) Error handling

- [ ] Add `@ControllerAdvice` to normalize error responses to `ErrorResponse`.
  - Translate validation errors to **400**.
  - Translate not found to **404**.
  - Translate integrity/conflict to **409**.

## 8) Database migrations & seed data

- [ ] Confirm `V1__init.sql` already contains:
  - Core tables.
  - Facility/address city enforcement trigger.
  - Indexes needed for public filters.
- [ ] Add `V2__reference_data.sql` for initial reference values.
- [ ] Optional: `V3__seed_dev.sql` for local dev data.

## 9) Testing checklist

- [ ] Unit tests for service validation (address-city consistency, URL validation).
- [ ] Integration tests for admin CRUD (happy paths + conflict cases).
- [ ] Smoke tests for public API to ensure unchanged behavior.
