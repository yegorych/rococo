create table if not exists `artist`
(
    id        binary(16)    unique not null default (UUID_TO_BIN(UUID(), true)),
    name      varchar(255)  unique not null,
    biography varchar(2000) not null,
    photo     longblob,
    primary key (id)
    );