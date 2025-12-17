create table container_productos
(
    container_id bigint not null
        references containers
            on delete cascade,
    producto_id  bigint not null
        references productos
            on delete cascade,
    primary key (container_id, producto_id)
);

