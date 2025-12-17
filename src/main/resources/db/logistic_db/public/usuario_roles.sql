create table usuario_roles
(
    usuario_id bigint       not null
        references usuarios
            on delete cascade,
    role       varchar(255) not null,
    primary key (usuario_id, role)
);

