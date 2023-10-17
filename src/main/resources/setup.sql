create table public.account (
                                id uuid not null,
                                user_id uuid not null,
                                name varchar(255) not null,
                                primary key (id)
);
create table public.app_user (
                                 uuid uuid not null,
                                 email varchar(255) not null unique,
                                 password varchar(255) not null,
                                 role varchar(255) not null check (role in ('STANDARD','ADMIN')),
                                 primary key (uuid)
);
create table action (
                        quantity float(53) not null,
                        datetime timestamp(6) not null,
                        price_in_cents bigint not null,
                        id uuid not null,
                        product_id uuid not null,
                        note varchar(255),
                        primary key (id)
);
create table category (
                          account_id uuid not null,
                          id uuid not null,
                          parent_category_id uuid,
                          description varchar(255),
                          name varchar(255) not null,
                          primary key (id)
);
create table product (
                         default_unit_multiplier float(53) not null,
                         category_id uuid not null,
                         id uuid not null,
                         name varchar(255) not null,
                         unit varchar(255) not null check (unit in ('GRAM','MILLILITRE','DAY','PIECE')),
                         primary key (id)
);
create table refresh_token (
                               exp timestamp(6) not null,
                               id uuid not null,
                               user_id uuid not null,
                               primary key (id)
);
alter table if exists public.account
    add constraint FKjajia7qudllc01cly9yddon8u
    foreign key (user_id)
    references public.app_user;
alter table if exists action
    add constraint FKodomlay394rf4ao59f8cpkh0v
    foreign key (product_id)
    references product;
alter table if exists category
    add constraint FK6ymhc01g9q7834m0220ruu83m
    foreign key (account_id)
    references public.account;
alter table if exists category
    add constraint FKs2ride9gvilxy2tcuv7witnxc
    foreign key (parent_category_id)
    references category;
alter table if exists product
    add constraint FK1mtsbur83frn64de7balymq9s
    foreign key (category_id)
    references category;
alter table if exists refresh_token
    add constraint FK5wkt2p042y3lwltk29cvpxuh
    foreign key (user_id)
    references public.app_user
    on delete cascade;