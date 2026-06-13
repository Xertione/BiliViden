create table user (
    id bigint primary key auto_increment,
    username varchar(64) not null unique,
    password_hash varchar(255) not null,
    nickname varchar(64) not null,
    status varchar(32) not null default 'ACTIVE',
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp,
    deleted tinyint not null default 0
);

create table user_bili_account (
    id bigint primary key auto_increment,
    user_id bigint not null,
    bili_uid varchar(64) not null,
    cookie_snapshot text not null,
    bind_status varchar(32) not null default 'BOUND',
    last_sync_time datetime null,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp,
    deleted tinyint not null default 0
);

create table video (
    id bigint primary key auto_increment,
    bvid varchar(32) not null unique,
    title varchar(255) not null,
    author_name varchar(128) not null,
    cover_url varchar(512),
    intro text,
    publish_time datetime,
    duration_seconds int,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp,
    deleted tinyint not null default 0
);

create table user_video_source (
    id bigint primary key auto_increment,
    user_id bigint not null,
    video_id bigint not null,
    source_type varchar(32) not null,
    source_time datetime null,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp,
    deleted tinyint not null default 0,
    unique key uk_user_video_source (user_id, video_id, source_type)
);

create table video_analysis_task (
    id bigint primary key auto_increment,
    user_id bigint not null,
    video_id bigint not null,
    analysis_type varchar(32) not null,
    status varchar(32) not null,
    retry_count int not null default 0,
    error_message varchar(1000),
    model_name varchar(128),
    prompt_version varchar(64),
    started_at datetime null,
    finished_at datetime null,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp,
    deleted tinyint not null default 0
);

create table video_analysis_result (
    id bigint primary key auto_increment,
    task_id bigint not null,
    summary text not null,
    core_points_json json not null,
    keywords_json json not null,
    controversies_json json not null,
    attitude_suggestion text,
    source_basis_json json not null,
    raw_response text,
    parsed_result json,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp,
    deleted tinyint not null default 0
);

create table knowledge_card (
    id bigint primary key auto_increment,
    user_id bigint not null,
    video_id bigint not null,
    analysis_task_id bigint not null,
    analysis_result_id bigint not null,
    title varchar(255) not null,
    summary text not null,
    key_points_json json not null,
    tags_json json not null,
    created_at datetime not null default current_timestamp,
    updated_at datetime not null default current_timestamp,
    deleted tinyint not null default 0
);
