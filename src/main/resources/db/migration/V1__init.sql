create extension if not exists "uuid-ossp";

create type facility_status as enum ('ACTIVE', 'HIDDEN', 'CLOSED');
create type contact_type as enum ('PHONE', 'EMAIL', 'WEBSITE', 'INSTAGRAM', 'FACEBOOK', 'TELEGRAM', 'WHATSAPP', 'OTHER');
create type media_kind as enum ('IMAGE');
create type price_kind as enum ('FROM_HOURLY', 'RANGE_HOURLY', 'FROM_DAILY', 'PER_EVENT', 'CONTACT');
create type equipment_mode as enum ('IN_HOUSE', 'RENTABLE', 'ON_REQUEST');
create type feature_value_type as enum ('BOOL', 'TEXT', 'NUMBER');

create table city (
    id uuid primary key,
    name text not null,
    country_code char(2) not null,
    center_lat numeric(9, 6),
    center_lng numeric(9, 6)
);

create unique index if not exists city_country_name_idx on city (country_code, name);

create table address (
    id uuid primary key,
    city_id uuid not null references city(id),
    label text not null,
    lat numeric(9, 6),
    lng numeric(9, 6)
);

create index if not exists address_city_idx on address (city_id);

create table media (
    id uuid primary key,
    url text not null,
    kind media_kind not null default 'IMAGE',
    created_at timestamptz not null default now()
);

create table facility (
    id uuid primary key,
    city_id uuid not null references city(id),
    name text not null,
    description text,
    address_id uuid references address(id),
    cover_media_id uuid references media(id),
    status facility_status not null default 'ACTIVE',
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index if not exists facility_city_status_idx on facility (city_id, status);
create index if not exists facility_name_lower_idx on facility (lower(name));
create index if not exists facility_search_idx on facility using gin (to_tsvector('simple', coalesce(name,'') || ' ' || coalesce(description,'')));

create table facility_media (
    facility_id uuid not null references facility(id) on delete cascade,
    media_id uuid not null references media(id) on delete cascade,
    sort_order int not null default 0,
    caption text,
    is_cover boolean not null default false,
    primary key (facility_id, media_id)
);

create index if not exists facility_media_sort_idx on facility_media (facility_id, sort_order);

create table contact_point (
    id uuid primary key,
    facility_id uuid not null references facility(id) on delete cascade,
    type contact_type not null,
    value text not null,
    label text,
    is_primary boolean not null default false
);

create index if not exists contact_point_facility_idx on contact_point (facility_id);

create table capability_type (
    id uuid primary key,
    code text unique not null,
    label text not null
);

create table facility_capability (
    facility_id uuid not null references facility(id) on delete cascade,
    capability_type_id uuid not null references capability_type(id),
    summary text,
    details_json jsonb,
    is_active boolean not null default true,
    primary key (facility_id, capability_type_id)
);

create index if not exists facility_capability_type_idx on facility_capability (capability_type_id);
create index if not exists facility_capability_facility_idx on facility_capability (facility_id);

create table space_type (
    id uuid primary key,
    code text unique not null,
    label text not null
);

create table space (
    id uuid primary key,
    facility_id uuid not null references facility(id) on delete cascade,
    space_type_id uuid not null references space_type(id),
    name text not null,
    description text,
    capacity_people int,
    size_m2 numeric(8, 2)
);

create index if not exists space_facility_idx on space (facility_id);
create index if not exists space_type_idx on space (space_type_id);
create index if not exists space_facility_type_idx on space (facility_id, space_type_id);

create table space_media (
    space_id uuid not null references space(id) on delete cascade,
    media_id uuid not null references media(id) on delete cascade,
    sort_order int not null default 0,
    caption text,
    primary key (space_id, media_id)
);

create index if not exists space_media_sort_idx on space_media (space_id, sort_order);

create table equipment_type (
    id uuid primary key,
    name text not null,
    category_code text not null,
    description text,
    cover_media_id uuid references media(id)
);

create index if not exists equipment_category_idx on equipment_type (category_code);
create index if not exists equipment_name_lower_idx on equipment_type (lower(name));

create table facility_equipment (
    facility_id uuid not null references facility(id) on delete cascade,
    equipment_type_id uuid not null references equipment_type(id),
    quantity int,
    mode equipment_mode not null,
    note text,
    primary key (facility_id, equipment_type_id)
);

create index if not exists facility_equipment_facility_idx on facility_equipment (facility_id);
create index if not exists facility_equipment_type_idx on facility_equipment (equipment_type_id);

create table feature (
    id uuid primary key,
    code text unique not null,
    label text not null,
    value_type feature_value_type not null
);

create table facility_feature (
    facility_id uuid not null references facility(id) on delete cascade,
    feature_id uuid not null references feature(id),
    value_bool boolean,
    value_text text,
    value_number numeric(12, 2),
    primary key (facility_id, feature_id)
);

create index if not exists facility_feature_feature_idx on facility_feature (feature_id);
create index if not exists facility_feature_facility_idx on facility_feature (facility_id);

create table opening_hours (
    facility_id uuid not null references facility(id) on delete cascade,
    day_of_week int not null check (day_of_week between 1 and 7),
    is_closed boolean not null default false,
    open_time time,
    close_time time,
    note text,
    primary key (facility_id, day_of_week)
);

create table price_info (
    id uuid primary key,
    facility_id uuid not null references facility(id) on delete cascade,
    capability_type_id uuid references capability_type(id),
    space_id uuid references space(id),
    kind price_kind not null,
    amount_from numeric(12, 2),
    amount_to numeric(12, 2),
    currency char(3) not null default 'EUR',
    note text
);

create index if not exists price_info_facility_idx on price_info (facility_id);
create index if not exists price_info_capability_idx on price_info (capability_type_id);
create index if not exists price_info_space_idx on price_info (space_id);
create index if not exists price_info_amount_idx on price_info (amount_from, amount_to);

create or replace function enforce_facility_address_city() returns trigger as $$
begin
    if new.address_id is not null then
        if not exists (
            select 1 from address a where a.id = new.address_id and a.city_id = new.city_id
        ) then
            raise exception 'Facility address must belong to the same city';
        end if;
    end if;
    return new;
end;
$$ language plpgsql;

create trigger facility_address_city_check
before insert or update on facility
for each row execute function enforce_facility_address_city();
