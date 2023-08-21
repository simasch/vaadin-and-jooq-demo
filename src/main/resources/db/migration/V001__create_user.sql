create sequence user_seq;

create table application_user
(
    id              bigint       NOT NULL DEFAULT NEXTVAL('user_seq') PRIMARY KEY,
    username        varchar(100) not null,
    name            varchar(100) not null,
    hashed_password varchar(100) not null
);

create table user_roles
(
    user_id bigint      not null,
    role    varchar(30) not null
);

ALTER TABLE user_roles
    ADD PRIMARY KEY (user_id, role);
ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_rolse_application_user FOREIGN KEY (user_id) REFERENCES application_user (id);
