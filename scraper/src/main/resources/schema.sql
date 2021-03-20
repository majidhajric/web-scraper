drop table if exists link cascade;
drop table if exists tags cascade;

create table link
(
    id bigserial not null
        constraint links_pk
            primary key,
    user_id varchar(36) not null,
    url varchar not null,
    hash varchar(32) not null,
    title varchar(128) not null,
    created_at timestamp,
    constraint links_pk_user_hash
        unique (hash, user_id)
);

create unique index links_user_id_hash_uindex
    on link (user_id, hash);

create table tags
(
    link_id bigint not null
        constraint link_tags___fk_links
            references link
            on delete cascade,
    tag varchar not null
);





