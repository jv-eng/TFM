CREATE TABLE UsuarioCredenciales (
    NombreUsuario VARCHAR(255) NOT NULL,
    Correo VARCHAR(255),
    Password VARCHAR(255),
    Clave VARCHAR(500),
    PRIMARY KEY (NombreUsuario)
);

CREATE TABLE Usuario (
    NombreUsuario VARCHAR(255) NOT NULL,
    CorreoElectronico VARCHAR(255),
    Socket VARCHAR(255),
    PRIMARY KEY (NombreUsuario)
);

CREATE TABLE Suscripcion (
    SuscripcionID VARCHAR(255) NOT NULL,
    usuario_NombreUsuario VARCHAR(255),
    NombreCanal VARCHAR(255),
    FechaSuscripcion DATETIME,
    ip VARCHAR(255),
    puerto INT,
    PRIMARY KEY (SuscripcionID),
    FOREIGN KEY (usuario_NombreUsuario) REFERENCES Usuario(NombreUsuario),
    FOREIGN KEY (NombreCanal) REFERENCES Canal(NombreCanal)
);

CREATE TABLE Canal (
    NombreCanal VARCHAR(255) NOT NULL,
    CreadorID VARCHAR(255),
    PRIMARY KEY (NombreCanal),
    FOREIGN KEY (CreadorID) REFERENCES Usuario(NombreUsuario)
);

CREATE TABLE Archivos (
    archivo_id BIGINT AUTO_INCREMENT,
    nombreArchivo VARCHAR(255),
    RutaSistemaArchivos VARCHAR(255),
    UsuarioID VARCHAR(255),
    CanalID VARCHAR(255),
    FechaEnvio DATETIME,
    PRIMARY KEY (archivo_id),
    FOREIGN KEY (UsuarioID) REFERENCES Usuario(NombreUsuario),
    FOREIGN KEY (CanalID) REFERENCES Canal(NombreCanal)
);
