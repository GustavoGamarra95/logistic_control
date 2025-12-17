create table detalle_factura
(
    id              bigserial
        primary key,
    factura_id      bigint           not null
        references facturas
            on delete cascade,
    producto_id     bigint
                                     references productos
                                         on delete set null,
    descripcion     varchar(500)     not null,
    cantidad        integer          not null,
    unidad_medida   varchar(20),
    precio_unitario double precision not null,
    descuento       double precision default 0,
    subtotal        double precision,
    porcentaje_iva  integer,
    monto_iva       double precision,
    total           double precision,
    codigo_ncm      varchar(20),
    observaciones   varchar(500)
);

