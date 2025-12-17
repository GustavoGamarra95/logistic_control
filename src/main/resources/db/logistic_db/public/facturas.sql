create table facturas
(
    id                     bigserial
        primary key,
    numero_factura         varchar(50)
        unique,
    fecha_emision          timestamp        default CURRENT_TIMESTAMP             not null,
    fecha_vencimiento      date,
    cliente_id             bigint                                                 not null
        references clientes
            on delete restrict,
    pedido_id              bigint
                                                                                  references pedidos
                                                                                      on delete set null,
    subtotal               double precision                                       not null,
    iva_5                  double precision,
    iva_10                 double precision,
    total_iva              double precision,
    total                  double precision                                       not null,
    descuento              double precision default 0,
    moneda                 varchar(10)      default 'PYG'::character varying,
    tipo_cambio            double precision,
    estado                 varchar(255)     default 'BORRADOR'::character varying not null,
    cdc                    varchar(44)
        unique,
    timbrado               varchar(20),
    establecimiento        varchar(10),
    punto_expedicion       varchar(10),
    tipo_documento         varchar(10)      default '1'::character varying,
    fecha_envio_sifen      timestamp,
    fecha_aprobacion_sifen timestamp,
    xml_de                 text,
    xml_de_firmado         text,
    respuesta_sifen        text,
    codigo_estado_sifen    varchar(10),
    mensaje_sifen          varchar(500),
    qr_code                text,
    url_kude               varchar(500),
    saldo                  double precision,
    pagado                 double precision default 0,
    observaciones          varchar(2000),
    created_at             timestamp        default CURRENT_TIMESTAMP             not null,
    updated_at             timestamp        default CURRENT_TIMESTAMP,
    deleted_at             timestamp,
    is_active              boolean          default true
);

create index idx_factura_numero
    on facturas (numero_factura);

create index idx_factura_cdc
    on facturas (cdc);

create index idx_factura_cliente
    on facturas (cliente_id);

create index idx_factura_fecha
    on facturas (fecha_emision);

create index idx_factura_estado
    on facturas (estado);

