create table if not exists `museum`
(
    id          binary(16)    unique not null default (UUID_TO_BIN(UUID(), true)),
    title       varchar(255)  unique not null,
    description varchar(1000),
    city        varchar(255),
    photo       longblob,
    country_id  binary(16)     not null,
    primary key (id)
    );