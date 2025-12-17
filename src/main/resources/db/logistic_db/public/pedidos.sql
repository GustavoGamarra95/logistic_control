create table pedidos
(
    id                     bigserial
        primary key,
    cliente_id             bigint                                               not null
        references clientes
            on delete restrict,
    fecha_registro         timestamp    default CURRENT_TIMESTAMP               not null,
    tipo_carga             varchar(255),
    pais_origen            varchar(100)                                         not null,
    pais_destino           varchar(100)                                         not null,
    ciudad_origen          varchar(100),
    ciudad_destino         varchar(100),
    descripcion_mercaderia varchar(1000)                                        not null,
    numero_contenedor_guia varchar(100),
    estado                 varchar(255) default 'REGISTRADO'::character varying not null,
    codigo_tracking        varchar(50)
        unique,
    fecha_estimada_llegada date,
    fecha_llegada_real     date,
    peso_total_kg          double precision,
    volumen_total_m3       double precision,
    valor_declarado        double precision,
    moneda                 varchar(10),
    numero_bl_awb          varchar(100),
    puerto_embarque        varchar(100),
    puerto_destino         varchar(100),
    empresa_transporte     varchar(200),
    observaciones          varchar(2000),
    requiere_seguro        boolean      default false,
    valor_seguro           double precision,
    container_id           bigint
                                                                                references containers
                                                                                    on delete set null,
    created_at             timestamp    default CURRENT_TIMESTAMP               not null,
    updated_at             timestamp    default CURRENT_TIMESTAMP,
    deleted_at             timestamp,
    is_active              boolean      default true,
    direccion_entrega      varchar(500),
    fecha_entrega          timestamp(6),
    fecha_entrega_estimada timestamp(6),
    fecha_pedido           timestamp(6),
    forma_pago             varchar(50),
    iva                    numeric(15, 2),
    sub_total              numeric(15, 2),
    total                  numeric(15, 2)
);

create index idx_pedido_tracking
    on pedidos (codigo_tracking);

create index idx_pedido_fecha
    on pedidos (fecha_registro);

create index idx_pedido_estado
    on pedidos (estado);

