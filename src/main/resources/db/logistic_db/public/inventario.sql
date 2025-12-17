create table inventario
(
    id                         bigserial
        primary key,
    cliente_id                 bigint                                                not null
        references clientes
            on delete restrict,
    container_id               bigint
                                                                                     references containers
                                                                                         on delete set null,
    producto_id                bigint                                                not null
        references productos
            on delete restrict,
    pedido_id                  bigint
                                                                                     references pedidos
                                                                                         on delete set null,
    ubicacion_deposito         varchar(100),
    zona                       varchar(50),
    pasillo                    varchar(20),
    rack                       varchar(20),
    nivel                      varchar(20),
    cantidad                   integer                                               not null,
    cantidad_reservada         integer      default 0,
    cantidad_disponible        integer,
    estado                     varchar(255) default 'EN_TRANSITO'::character varying not null,
    fecha_entrada              timestamp,
    fecha_salida               timestamp,
    lote                       varchar(100),
    fecha_vencimiento          timestamp,
    numero_declaracion_aduanal varchar(100),
    fecha_despacho_aduana      timestamp,
    dias_almacenaje            integer,
    costo_almacenaje_diario    double precision,
    costo_almacenaje_total     double precision,
    observaciones              varchar(1000),
    created_at                 timestamp    default CURRENT_TIMESTAMP                not null,
    updated_at                 timestamp    default CURRENT_TIMESTAMP,
    deleted_at                 timestamp,
    is_active                  boolean      default true
);

create index idx_inventario_cliente
    on inventario (cliente_id);

create index idx_inventario_producto
    on inventario (producto_id);

create index idx_inventario_ubicacion
    on inventario (ubicacion_deposito);

create index idx_inventario_estado
    on inventario (estado);

