create sequence person_seq;

create table person
(
    id            bigint       NOT NULL DEFAULT NEXTVAL('person_seq') PRIMARY KEY,
    first_name    varchar(100) not null,
    last_name     varchar(100) not null,
    email         varchar(200) not null,
    phone         varchar(30),
    date_of_birth date         not null,
    role          varchar(100),
    occupation    varchar(100),
    important     bool,
    version       int
);
