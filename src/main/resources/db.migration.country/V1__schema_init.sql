create extension if not exists "uuid-ossp";

create table if not exists "country"
(
    id          UUID unique        not null default uuid_generate_v1() primary key,
    country     varchar(255)       not null,
    country_code varchar(2) unique not null
);

alter table "country" owner to postgres;

delete from "country";

insert into "country"(country, country_code)
values
    ('Fiji', 'FJ'),
    ('Tanzania', 'TZ'),
    ('Russia', 'RU');
