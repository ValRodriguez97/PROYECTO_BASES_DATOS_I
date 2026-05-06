DROP DATABASE IF EXISTS mundial2026;
CREATE DATABASE mundial2026 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mundial2026;

CREATE TABLE Pais (
    idPais INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    continente VARCHAR(100) NOT NULL,
    esAnfitrion BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE Confederacion (
    idConfederacion INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    sigla VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE DirectorTecnico (
    idDt INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    nacionalidad VARCHAR(100) NOT NULL,
    fechaNacimiento DATE NOT NULL
);

CREATE TABLE Equipo (
    idEquipo INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    rankingFifa INT NOT NULL,
    idPais INT NOT NULL,
    idConfederacion INT NOT NULL,
    idDt INT NOT NULL,
    FOREIGN KEY (idPais) REFERENCES Pais(idPais),
    FOREIGN KEY (idConfederacion) REFERENCES Confederacion(idConfederacion),
    FOREIGN KEY (idDt) REFERENCES DirectorTecnico(idDt)
);

CREATE TABLE Posicion (
    idPosicion INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE Jugador (
    idJugador INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    fechaNacimiento DATE NOT NULL,
    estatura DECIMAL(4,2) NOT NULL,
    peso DECIMAL(5,2) NOT NULL,
    valorMercado DECIMAL(15,2) NOT NULL,
    idEquipo INT NOT NULL,
    idPosicion INT NOT NULL,
    FOREIGN KEY (idEquipo) REFERENCES Equipo(idEquipo),
    FOREIGN KEY (idPosicion) REFERENCES Posicion(idPosicion)
);

CREATE TABLE Grupo (
    idGrupo INT AUTO_INCREMENT PRIMARY KEY,
    letra CHAR(1) NOT NULL UNIQUE
);

CREATE TABLE EquipoGrupo (
    idGrupo INT NOT NULL,
    idEquipo INT NOT NULL,
    PRIMARY KEY (idGrupo, idEquipo),
    FOREIGN KEY (idGrupo) REFERENCES Grupo(idGrupo),
    FOREIGN KEY (idEquipo) REFERENCES Equipo(idEquipo)
);

CREATE TABLE Ciudad (
    idCiudad INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    idPais INT NOT NULL,
    FOREIGN KEY (idPais) REFERENCES Pais(idPais)
);

CREATE TABLE Estadio (
    idEstadio INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    capacidad INT NOT NULL,
    idCiudad INT NOT NULL,
    FOREIGN KEY (idCiudad) REFERENCES Ciudad(idCiudad)
);

CREATE TABLE Partido (
    idPartido INT AUTO_INCREMENT PRIMARY KEY,
    horaFecha DATETIME NOT NULL,
    idGrupo INT NOT NULL,
    idEstadio INT NOT NULL,
    FOREIGN KEY (idGrupo) REFERENCES Grupo(idGrupo),
    FOREIGN KEY (idEstadio) REFERENCES Estadio(idEstadio)
);

CREATE TABLE DetallePartido (
    idPartido INT NOT NULL,
    idEquipo INT NOT NULL,
    condicion ENUM('LOCAL', 'VISITANTE') NOT NULL,
    golesAnotados INT DEFAULT 0,
    PRIMARY KEY (idPartido, idEquipo),
    FOREIGN KEY (idPartido) REFERENCES Partido(idPartido) ON DELETE CASCADE,
    FOREIGN KEY (idEquipo) REFERENCES Equipo(idEquipo),
    UNIQUE (idPartido, condicion)
);

CREATE TABLE Usuario (
    idUsuario INT AUTO_INCREMENT PRIMARY KEY,
    nombreUsuario VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    tipoUsuario ENUM('ADMINISTRADOR', 'TRADICIONAL', 'ESPORADICO') NOT NULL
);

CREATE TABLE Bitacora (
    idBitacora INT AUTO_INCREMENT PRIMARY KEY,
    idUsuario INT NOT NULL,
    fechaHoraIngreso DATETIME NOT NULL,
    fechaHoraSalida DATETIME NULL,
    FOREIGN KEY (idUsuario) REFERENCES Usuario(idUsuario)
);

-- ============================================================
-- TRIGGERS DE INTEGRIDAD DE NEGOCIO
-- ============================================================

-- Solo puede existir UN administrador en el sistema.
DELIMITER $$
CREATE TRIGGER trg_unico_admin_insert
BEFORE INSERT ON Usuario
FOR EACH ROW
BEGIN
    IF NEW.tipoUsuario = 'ADMINISTRADOR'
       AND (SELECT COUNT(*) FROM Usuario WHERE tipoUsuario = 'ADMINISTRADOR') > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Solo puede existir un Administrador en el sistema.';
    END IF;
END$$

CREATE TRIGGER trg_unico_admin_update
BEFORE UPDATE ON Usuario
FOR EACH ROW
BEGIN
    IF NEW.tipoUsuario = 'ADMINISTRADOR' AND OLD.tipoUsuario <> 'ADMINISTRADOR'
       AND (SELECT COUNT(*) FROM Usuario WHERE tipoUsuario = 'ADMINISTRADOR') > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Solo puede existir un Administrador en el sistema.';
    END IF;
END$$

-- Cada grupo del Mundial tiene máximo 4 equipos.
CREATE TRIGGER trg_max_equipos_grupo
BEFORE INSERT ON EquipoGrupo
FOR EACH ROW
BEGIN
    IF (SELECT COUNT(*) FROM EquipoGrupo WHERE idGrupo = NEW.idGrupo) >= 4 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Un grupo no puede tener mas de 4 equipos.';
    END IF;
END$$

DELIMITER ;