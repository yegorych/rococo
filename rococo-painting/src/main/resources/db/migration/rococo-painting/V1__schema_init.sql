create table if not exists `painting`
(
    id          binary(16)      unique not null default (UUID_TO_BIN(UUID(), true)),
    title       varchar(255)    not null,
    description varchar(1000),
    artist_id   binary(16)     not null,
    museum_id   binary(16),
    content     longblob,
    primary key (id)
    );