create table if not exists `log`
(
    id  binary(16) unique  not null default (UUID_TO_BIN(UUID(), true)),
    username  varchar(50)  not null,
    painting_id   binary(16)     not null,
    event_type  varchar(50)  not null,
    event_time  DATETIME          NOT NULL,
    created_at  DATETIME          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    primary key (id)
);






