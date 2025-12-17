create table containers
(
    id                     bigserial
        primary key,
    numero                 varchar(50)                         not null
        unique,
    tipo                   varchar(255)                        not null,
    peso_kg                double precision,
    peso_maximo_kg         double precision,
    volumen_m3             double precision,
    volumen_maximo_m3      double precision,
    empresa_transporte     varchar(200),
    empresa_naviera        varchar(200),
    buque_nombre           varchar(200),
    viaje_numero           varchar(50),
    ruta                   varchar(500),
    puerto_origen          varchar(100),
    puerto_destino         varchar(100),
    fecha_salida           date,
    fecha_llegada_estimada date,
    fecha_llegada_real     date,
    consolidado            boolean   default false,
    en_transito            boolean   default false,
    en_puerto              boolean   default false,
    en_aduana              boolean   default false,
    liberado               boolean   default false,
    numero_bl              varchar(100),
    fecha_emision_bl       date,
    observaciones          varchar(2000),
    created_at             timestamp default CURRENT_TIMESTAMP not null,
    updated_at             timestamp default CURRENT_TIMESTAMP,
    deleted_at             timestamp,
    is_active              boolean   default true
);

create index idx_container_numero
    on containers (numero);

create index idx_container_fecha_salida
    on containers (fecha_salida);

