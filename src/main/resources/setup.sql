-- app_user - User.class
create table public.app_user (
    id uuid not null,
    email varchar(255) not null unique,
    password varchar(255) not null,
    role varchar(255) not null check (role in ('STANDARD','ADMIN')),
    primary key (id)
);

-- refresh_token - RefreshToken.class
CREATE TABLE refresh_token
(
    id      UUID                        NOT NULL,
    user_id UUID                        NOT NULL,
    exp     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_refresh_token PRIMARY KEY (id)
);

ALTER TABLE refresh_token
    ADD CONSTRAINT FK_REFRESH_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES public.app_user (id) ON DELETE CASCADE;