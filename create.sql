-- Crear base de datos
CREATE DATABASE IF NOT EXISTS datos_java;
USE datos_java;

-- TABLA ALMACEN (ahora con columna pais)
CREATE TABLE almacen (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    pais VARCHAR(100) NOT NULL
);

INSERT INTO almacen (nombre, pais) VALUES ('Madrid', 'España'), ('Londres', 'Inglaterra'), ('Berlin', 'Alemania'), ('Barcelona', 'España'), ('Manchester', 'Inglaterra'), ('Weimar', 'Alemania');


-- TABLA EMPLEADOS
CREATE TABLE empleados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    empleado VARCHAR(100) NOT NULL,
    departamento VARCHAR(100) NOT NULL
);

-- TABLA PRODUCTOS
CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    producto VARCHAR(100) NOT NULL,
    precio DOUBLE(10,2) NOT NULL,
    cantidad DOUBLE(10,2) NOT NULL,
    pais VARCHAR(100) NOT NULL,
    id_almacen INT NOT NULL,
    FOREIGN KEY (id_almacen) REFERENCES almacen(id)
);

-- TABLA USUARIOS
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(100) NOT NULL UNIQUE,
    contraseña VARCHAR(100) NOT NULL,
    rol VARCHAR(50) NOT NULL
);

-- Insertar usuario admin de prueba
INSERT INTO usuarios (usuario, contraseña, rol) VALUES
('admin', 'admin', 'admin');
