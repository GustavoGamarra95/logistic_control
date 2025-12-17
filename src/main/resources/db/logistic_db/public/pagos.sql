create table pagos
(
    id            bigserial
        primary key,
    factura_id    bigint                                not null
        references facturas
            on delete restrict,
    monto         double precision                      not null,
    fecha_pago    timestamp   default CURRENT_TIMESTAMP not null,
    metodo_pago   varchar(50),
    referencia    varchar(100),
    banco         varchar(100),
    numero_cuenta varchar(50),
    moneda        varchar(10) default 'PYG'::character varying,
    tipo_cambio   double precision,
    observaciones varchar(500)
);

create index idx_pago_factura
    on pagos (factura_id);

create index idx_pago_fecha
    on pagos (fecha_pago);

