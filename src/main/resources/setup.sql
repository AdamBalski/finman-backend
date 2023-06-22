-- app_user - User.class
create table public.app_user (
    id uuid not null,
    email varchar(255) not null unique,
    password varchar(255) not null,
    role varchar(255) not null check (role in ('STANDARD','ADMIN')),
    primary key (id)
)