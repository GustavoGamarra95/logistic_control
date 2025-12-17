create table container_clientes
(
    container_id bigint not null
        references containers
            on delete cascade,
    cliente_id   bigint not null
        references clientes
            on delete cascade,
    primary key (container_id, cliente_id)
);

