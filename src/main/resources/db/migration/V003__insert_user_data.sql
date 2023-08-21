insert into application_user (id, username,name,hashed_password) values ('1','user','John Normal','$2a$10$xdbKoM48VySZqVSU/cSlVeJn0Z04XCZ7KZBjUBC00eKo5uLswyOpe');
insert into user_roles (user_id, role) values ('1', 'USER');
insert into application_user (id, username,name,hashed_password) values ('2','admin','Emma Powerful','$2a$10$jpLNVNeA7Ar/ZQ2DKbKCm.MuT2ESe.Qop96jipKMq7RaUgCoQedV.');
insert into user_roles (user_id, role) values ('2', 'USER');
insert into user_roles (user_id, role) values ('2', 'ADMIN');
