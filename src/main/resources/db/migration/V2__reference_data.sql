insert into capability_type (id, code, label) values
    (uuid_generate_v4(), 'REHEARSAL', 'Rehearsal'),
    (uuid_generate_v4(), 'RECORDING', 'Recording'),
    (uuid_generate_v4(), 'CONCERT', 'Concert'),
    (uuid_generate_v4(), 'EQUIPMENT_RENTAL', 'Equipment rental');

insert into feature (id, code, label, value_type) values
    (uuid_generate_v4(), 'PARKING', 'Parking', 'BOOL'),
    (uuid_generate_v4(), 'LATE_NIGHT', 'Late night', 'BOOL'),
    (uuid_generate_v4(), 'AIRCON', 'Air conditioning', 'BOOL'),
    (uuid_generate_v4(), 'SOUNDPROOF', 'Soundproof', 'BOOL'),
    (uuid_generate_v4(), 'WHEELCHAIR_ACCESS', 'Wheelchair access', 'BOOL'),
    (uuid_generate_v4(), 'DELIVERY_AVAILABLE', 'Delivery available', 'BOOL');

insert into space_type (id, code, label) values
    (uuid_generate_v4(), 'REHEARSAL_ROOM', 'Rehearsal room'),
    (uuid_generate_v4(), 'LIVE_ROOM', 'Live room'),
    (uuid_generate_v4(), 'CONTROL_ROOM', 'Control room'),
    (uuid_generate_v4(), 'STAGE', 'Stage');

insert into equipment_type (id, name, category_code, description) values
    (uuid_generate_v4(), '5-piece drum kit', 'DRUMS', 'Standard rehearsal kit'),
    (uuid_generate_v4(), 'PA system', 'PA', 'Full-range PA'),
    (uuid_generate_v4(), 'SM58 microphone', 'MIC', 'Dynamic vocal mic'),
    (uuid_generate_v4(), 'Guitar amp', 'AMPS', 'Combo amplifier'),
    (uuid_generate_v4(), 'Stage lighting set', 'LIGHTS', 'LED light bars'),
    (uuid_generate_v4(), 'Electric guitar', 'GUITARS', 'Standard electric guitar'),
    (uuid_generate_v4(), 'Digital piano', 'KEYS', '88-key digital piano'),
    (uuid_generate_v4(), 'DJ controller', 'DJ', '2-deck controller');
