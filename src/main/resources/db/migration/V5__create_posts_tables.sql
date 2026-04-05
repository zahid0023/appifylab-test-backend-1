create table if not exists posts
(
    id         bigserial
        primary key,
    user_id    bigint                                             not null
        references users
            on delete cascade,
    content    text,
    is_public  boolean                  default true             not null,
    created_by bigint                                            not null,
    created_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    updated_by bigint                                            not null,
    updated_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    version    bigint                   default 0                not null,
    is_active  boolean                  default true             not null,
    is_deleted boolean                  default false            not null,
    deleted_by bigint,
    deleted_at timestamp with time zone
);

create table if not exists post_images
(
    id         bigserial
        primary key,
    post_id    bigint                                             not null
        references posts
            on delete cascade,
    image_url  text                                               not null,
    is_default boolean                  default false            not null,
    created_by bigint                                            not null,
    created_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    updated_by bigint                                            not null,
    updated_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    version    bigint                   default 0                not null,
    is_active  boolean                  default true             not null,
    is_deleted boolean                  default false            not null,
    deleted_by bigint,
    deleted_at timestamp with time zone
);
