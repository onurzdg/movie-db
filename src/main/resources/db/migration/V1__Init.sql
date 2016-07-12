create table privilege (
 id smallint not null primary key,
 title text not null
);

insert into privilege (id, title)
values (0, 'regular'),(1, 'admin');

create table account_state (
 id smallint not null primary key,
 title text not null
);

insert into account_state (id, title)
values (0, 'active'),(1, 'suspended');

create table digital_copy (
 id smallint not null primary key,
 title text not null
);

insert into digital_copy (id, title)
values (0, 'youtube'),(1, 'amazon');

create table user_account (
 id bigserial primary key,
 name text not null,
 email text unique not null,
 password text not null,
 created_at timestamptz not null default now(),
 updated_at timestamptz,
 suspended_at timestamptz,
 privilege_id smallint not null references privilege(id),
 account_state_id smallint not null references account_state(id)
);

create index user_idx_email_password on user_account(email, password);

create table session (
 id text primary key,
 start_at timestamptz not null default now(),
 end_at timestamptz not null,
 ip_address text not null,
 user_id bigint not null references user_account(id)
);

create table movie (
 id bigserial primary key,
 title text not null,
 year smallint not null,
 imdbRating float not null
);

create index movie_idx_title on movie(title);
create index movie_idx_year on movie(year);

create table user_movie_collection (
 user_id bigint key references user_account(id),
 movie_id bigint key references movie(id),
 digital_copy_id smallint references digital_copy(id),
 physical_copy_location text,
 personalRating float,
 watched_at timestamptz not null,
 comment text,
 constraint user_movies_pkey primary key (user_id, movie_id)
);

create table watch_list (
 user_id bigint key references user_account(id),
 movie_id bigint key references movie(id),
 constraint user_movies_pkey primary key (user_id, movie_id)
);

