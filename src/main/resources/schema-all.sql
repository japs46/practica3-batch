DROP TABLE IF EXISTS persona;

CREATE TABLE persona (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(20),
    apellido VARCHAR(20),
    telefono VARCHAR(10)
);