create table if not exists post_likes
(
    id         bigserial
        primary key,
    post_id    bigint                                             not null
        references posts on delete cascade,
    user_id    bigint                                             not null
        references users on delete cascade,
    is_like    boolean                  default true             not null,
    created_by bigint                                            not null,
    created_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    updated_by bigint                                            not null,
    updated_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    version    bigint                   default 0                not null,
    is_active  boolean                  default true             not null,
    is_deleted boolean                  default false            not null,
    deleted_by bigint,
    deleted_at timestamp with time zone,
    unique (post_id, user_id)
);
