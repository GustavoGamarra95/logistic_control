create table historial_estados
(
    id              bigserial
        primary key,
    pedido_id       bigint                              not null
        references pedidos
            on delete cascade,
    estado_anterior varchar(255),
    estado_nuevo    varchar(255)                        not null,
    fecha_cambio    timestamp default CURRENT_TIMESTAMP not null,
    comentario      varchar(1000),
    usuario         varchar(100)
);

create index idx_historial_pedido
    on historial_estados (pedido_id);

create index idx_historial_fecha
    on historial_estados (fecha_cambio);

