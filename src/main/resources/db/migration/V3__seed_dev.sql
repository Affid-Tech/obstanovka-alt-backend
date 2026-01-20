insert into city (id, name, country_code, center_lat, center_lng) values
    ('11111111-1111-1111-1111-111111111111', 'Berlin', 'DE', 52.520008, 13.404954),
    ('22222222-2222-2222-2222-222222222222', 'Luxembourg', 'LU', 49.611622, 6.131935);

insert into address (id, city_id, label, lat, lng) values
    ('aaaaaaaa-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 'Kreuzberg, Berlin', 52.498600, 13.418400),
    ('aaaaaaaa-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', 'Mitte, Berlin', 52.520800, 13.409500),
    ('bbbbbbbb-0000-0000-0000-000000000001', '22222222-2222-2222-2222-222222222222', 'Grund, Luxembourg', 49.608300, 6.132100);

insert into media (id, url) values
    ('cccccccc-0000-0000-0000-000000000001', 'https://images.example.com/berlin-rehearsal-cover.jpg'),
    ('cccccccc-0000-0000-0000-000000000002', 'https://images.example.com/berlin-studio-cover.jpg'),
    ('cccccccc-0000-0000-0000-000000000003', 'https://images.example.com/lux-stage-cover.jpg'),
    ('cccccccc-0000-0000-0000-000000000004', 'https://images.example.com/berlin-rehearsal-gallery1.jpg'),
    ('cccccccc-0000-0000-0000-000000000005', 'https://images.example.com/berlin-rehearsal-gallery2.jpg');

insert into facility (id, city_id, name, description, address_id, cover_media_id, status) values
    ('dddddddd-0000-0000-0000-000000000001', '11111111-1111-1111-1111-111111111111', 'Kreuzberg Rehearsal Hub', '24/7 rehearsal rooms with backline.', 'aaaaaaaa-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000001', 'ACTIVE'),
    ('dddddddd-0000-0000-0000-000000000002', '11111111-1111-1111-1111-111111111111', 'Studio Mitte', 'Boutique recording studio for bands and podcasts.', 'aaaaaaaa-0000-0000-0000-000000000002', 'cccccccc-0000-0000-0000-000000000002', 'ACTIVE'),
    ('dddddddd-0000-0000-0000-000000000003', '22222222-2222-2222-2222-222222222222', 'Lux Live Stage', 'Live venue with full production.', 'bbbbbbbb-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000003', 'ACTIVE'),
    ('dddddddd-0000-0000-0000-000000000004', '22222222-2222-2222-2222-222222222222', 'City Sound Lab', 'Flexible rehearsal and workshop space.', null, null, 'ACTIVE');

insert into facility_media (facility_id, media_id, sort_order, caption, is_cover) values
    ('dddddddd-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000004', 1, 'Room overview', false),
    ('dddddddd-0000-0000-0000-000000000001', 'cccccccc-0000-0000-0000-000000000005', 2, 'Backline', false);

insert into contact_point (id, facility_id, type, value, label, is_primary) values
    (uuid_generate_v4(), 'dddddddd-0000-0000-0000-000000000001', 'PHONE', '+49 30 123456', 'Reception', true),
    (uuid_generate_v4(), 'dddddddd-0000-0000-0000-000000000002', 'EMAIL', 'hello@studiomitte.example', null, true),
    (uuid_generate_v4(), 'dddddddd-0000-0000-0000-000000000003', 'WEBSITE', 'https://luxlivestage.example', null, true);

insert into facility_capability (facility_id, capability_type_id, summary, details_json, is_active)
select 'dddddddd-0000-0000-0000-000000000001', id, 'Rehearsal rooms with full backline', '{"rooms":4}'::jsonb, true
from capability_type where code = 'REHEARSAL';

insert into facility_capability (facility_id, capability_type_id, summary, details_json, is_active)
select 'dddddddd-0000-0000-0000-000000000002', id, 'Analog + digital recording', '{"engineer":true}'::jsonb, true
from capability_type where code = 'RECORDING';

insert into facility_capability (facility_id, capability_type_id, summary, details_json, is_active)
select 'dddddddd-0000-0000-0000-000000000003', id, 'Concerts up to 500 people', '{"capacity":500}'::jsonb, true
from capability_type where code = 'CONCERT';

insert into facility_capability (facility_id, capability_type_id, summary, details_json, is_active)
select 'dddddddd-0000-0000-0000-000000000004', id, 'Small-scale rehearsal', null, true
from capability_type where code = 'REHEARSAL';

insert into facility_feature (facility_id, feature_id, value_bool)
select 'dddddddd-0000-0000-0000-000000000001', id, true
from feature where code in ('PARKING', 'SOUNDPROOF', 'LATE_NIGHT');

insert into facility_feature (facility_id, feature_id, value_bool)
select 'dddddddd-0000-0000-0000-000000000002', id, true
from feature where code in ('AIRCON', 'WHEELCHAIR_ACCESS');

insert into facility_feature (facility_id, feature_id, value_bool)
select 'dddddddd-0000-0000-0000-000000000003', id, true
from feature where code in ('PARKING', 'WHEELCHAIR_ACCESS');

insert into facility_feature (facility_id, feature_id, value_number)
select 'dddddddd-0000-0000-0000-000000000001', id, 110
from feature where code = 'MAX_VOLUME_DB';

insert into facility_feature (facility_id, feature_id, value_text)
select 'dddddddd-0000-0000-0000-000000000002', id, 'Please arrive 15 minutes early for setup.'
from feature where code = 'HOUSE_NOTES';

insert into space (id, facility_id, space_type_id, name, description, capacity_people, size_m2)
select 'eeeeeeee-0000-0000-0000-000000000001', 'dddddddd-0000-0000-0000-000000000001', id, 'Room A', 'Soundproof room with backline', 6, 22.5
from space_type where code = 'REHEARSAL_ROOM';

insert into space (id, facility_id, space_type_id, name, description, capacity_people, size_m2)
select 'eeeeeeee-0000-0000-0000-000000000002', 'dddddddd-0000-0000-0000-000000000002', id, 'Control Room', 'Mixing room', 4, 18.0
from space_type where code = 'CONTROL_ROOM';

insert into facility_equipment (facility_id, equipment_type_id, quantity, mode, note)
select 'dddddddd-0000-0000-0000-000000000001', id, 1, 'IN_HOUSE', 'Included in room rate'
from equipment_type where category_code = 'DRUMS' limit 1;

insert into facility_equipment (facility_id, equipment_type_id, quantity, mode, note)
select 'dddddddd-0000-0000-0000-000000000001', id, 2, 'IN_HOUSE', null
from equipment_type where category_code = 'AMPS' limit 1;

insert into facility_equipment (facility_id, equipment_type_id, quantity, mode, note)
select 'dddddddd-0000-0000-0000-000000000002', id, 1, 'IN_HOUSE', 'Included with engineer'
from equipment_type where category_code = 'MIC' limit 1;

insert into facility_equipment (facility_id, equipment_type_id, quantity, mode, note)
select 'dddddddd-0000-0000-0000-000000000003', id, 1, 'RENTABLE', 'Lighting package'
from equipment_type where category_code = 'LIGHTS' limit 1;

insert into opening_hours (facility_id, day_of_week, is_closed, open_time, close_time)
values
    ('dddddddd-0000-0000-0000-000000000001', 1, false, '10:00', '22:00'),
    ('dddddddd-0000-0000-0000-000000000001', 2, false, '10:00', '22:00'),
    ('dddddddd-0000-0000-0000-000000000002', 1, false, '09:00', '20:00'),
    ('dddddddd-0000-0000-0000-000000000003', 5, false, '12:00', '23:00');

insert into price_info (id, facility_id, kind, amount_from, amount_to, currency, note)
values
    (uuid_generate_v4(), 'dddddddd-0000-0000-0000-000000000001', 'FROM_HOURLY', 18.00, null, 'EUR', 'Off-peak rate'),
    (uuid_generate_v4(), 'dddddddd-0000-0000-0000-000000000002', 'FROM_DAILY', 250.00, null, 'EUR', 'Engineer included'),
    (uuid_generate_v4(), 'dddddddd-0000-0000-0000-000000000003', 'PER_EVENT', 500.00, 1200.00, 'EUR', 'Includes crew'),
    (uuid_generate_v4(), 'dddddddd-0000-0000-0000-000000000004', 'CONTACT', null, null, 'EUR', 'Request a quote');
