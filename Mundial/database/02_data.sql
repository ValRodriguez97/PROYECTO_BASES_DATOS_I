-- ============================================================
-- 02_data.sql  –  Datos del Mundial FIFA 2026
-- Ejecutar DESPUÉS de 01_schema.sql
-- ============================================================
USE mundial2026;

-- ============================================================
-- CONFEDERACIONES
-- ============================================================
INSERT INTO Confederacion (nombre, sigla) VALUES
  ('Union de Asociaciones Europeas de Futbol',       'UEFA'),
  ('Confederacion Sudamericana de Futbol',           'CONMEBOL'),
  ('Confederacion de Futbol de America del Norte',   'CONCACAF'),
  ('Confederacion Africana de Futbol',               'CAF'),
  ('Confederacion Asiatica de Futbol',               'AFC'),
  ('Federacion de Futbol de Oceania',                'OFC');

-- ============================================================
-- PAISES (sede + participantes relevantes)
-- ============================================================
INSERT INTO Pais (nombre, continente, esAnfitrion) VALUES
  ('Mexico',          'America del Norte', TRUE),
  ('Estados Unidos',  'America del Norte', TRUE),
  ('Canada',          'America del Norte', TRUE),
  -- UEFA
  ('Alemania',        'Europa',            FALSE),
  ('Francia',         'Europa',            FALSE),
  ('España',          'Europa',            FALSE),
  ('Portugal',        'Europa',            FALSE),
  ('Inglaterra',      'Europa',            FALSE),
  ('Holanda',         'Europa',            FALSE),
  ('Belgica',         'Europa',            FALSE),
  ('Italia',          'Europa',            FALSE),
  ('Croacia',         'Europa',            FALSE),
  ('Austria',         'Europa',            FALSE),
  ('Escocia',         'Europa',            FALSE),
  ('Suiza',           'Europa',            FALSE),
  ('Dinamarca',       'Europa',            FALSE),
  ('Turquia',         'Europa',            FALSE),
  ('Serbia',          'Europa',            FALSE),
  -- CONMEBOL
  ('Argentina',       'America del Sur',   FALSE),
  ('Brasil',          'America del Sur',   FALSE),
  ('Uruguay',         'America del Sur',   FALSE),
  ('Colombia',        'America del Sur',   FALSE),
  ('Chile',           'America del Sur',   FALSE),
  ('Ecuador',         'America del Sur',   FALSE),
  ('Venezuela',       'America del Sur',   FALSE),
  ('Bolivia',         'America del Sur',   FALSE),
  ('Paraguay',        'America del Sur',   FALSE),
  -- CONCACAF
  ('Costa Rica',      'America Central',   FALSE),
  ('Panama',          'America Central',   FALSE),
  ('Jamaica',         'America Central',   FALSE),
  ('Honduras',        'America Central',   FALSE),
  ('Guatemala',       'America Central',   FALSE),
  ('Cuba',            'America Central',   FALSE),
  -- CAF
  ('Marruecos',       'Africa',            FALSE),
  ('Senegal',         'Africa',            FALSE),
  ('Nigeria',         'Africa',            FALSE),
  ('Ghana',           'Africa',            FALSE),
  ('Camerun',         'Africa',            FALSE),
  ('Costa de Marfil', 'Africa',            FALSE),
  ('Tunez',           'Africa',            FALSE),
  ('Egipto',          'Africa',            FALSE),
  ('Mali',            'Africa',            FALSE),
  -- AFC
  ('Japon',           'Asia',              FALSE),
  ('Corea del Sur',   'Asia',              FALSE),
  ('Arabia Saudita',  'Asia',              FALSE),
  ('Iran',            'Asia',              FALSE),
  ('Australia',       'Oceania',           FALSE),
  ('China',           'Asia',              FALSE),
  -- OFC
  ('Nueva Zelanda',   'Oceania',           FALSE);

-- ============================================================
-- CIUDADES SEDES
-- ============================================================
INSERT INTO Ciudad (nombre, idPais) VALUES
  -- México
  ('Ciudad de Mexico',  1),
  ('Guadalajara',       1),
  ('Monterrey',         1),
  -- Estados Unidos
  ('Nueva York',        2),
  ('Los Angeles',       2),
  ('Dallas',            2),
  ('San Francisco',     2),
  ('Miami',             2),
  ('Seattle',           2),
  ('Boston',            2),
  ('Houston',           2),
  ('Kansas City',       2),
  -- Canadá
  ('Toronto',           3),
  ('Vancouver',         3);

-- ============================================================
-- ESTADIOS (12 sedes reales)
-- ============================================================
INSERT INTO Estadio (nombre, capacidad, idCiudad) VALUES
  ('Estadio Azteca',              87523, 1),  -- idEstadio=1
  ('Estadio Akron',               49850, 2),  -- 2
  ('Estadio BBVA',                51350, 3),  -- 3
  ('MetLife Stadium',             82500, 4),  -- 4
  ('SoFi Stadium',                70240, 5),  -- 5
  ('AT&T Stadium',                80000, 6),  -- 6
  ('Levi''s Stadium',             68500, 7),  -- 7
  ('Hard Rock Stadium',           64767, 8),  -- 8
  ('Lumen Field',                 72000, 9),  -- 9
  ('Gillette Stadium',            64628, 10), -- 10
  ('BMO Stadium',                 30500, 13), -- 11 Toronto
  ('BC Place',                    54500, 14); -- 12 Vancouver

-- ============================================================
-- DIRECTORES TÉCNICOS
-- ============================================================
INSERT INTO DirectorTecnico (nombre, apellido, nacionalidad, fechaNacimiento) VALUES
  ('Julian',      'Nagelsmann',  'Alemania',      '1987-07-23'),   -- 1
  ('Didier',      'Deschamps',   'Francia',        '1968-10-15'),  -- 2
  ('Luis',        'de la Fuente','España',         '1961-12-26'),  -- 3
  ('Roberto',     'Martinez',    'España',         '1973-07-13'),  -- 4 Portugal
  ('Gareth',      'Southgate',   'Inglaterra',     '1970-09-03'),  -- 5
  ('Ronald',      'Koeman',      'Holanda',        '1963-03-07'),  -- 6
  ('Domenico',    'Tedesco',     'Belgica',        '1985-09-12'),  -- 7
  ('Luciano',     'Spalletti',   'Italia',         '1959-03-07'),  -- 8
  ('Zlatko',      'Dalic',       'Croacia',        '1966-08-26'),  -- 9
  ('Ralf',        'Rangnick',    'Austria',        '1958-06-29'),  -- 10
  ('Steve',       'Clarke',      'Escocia',        '1963-08-29'),  -- 11
  ('Murat',       'Yakin',       'Suiza',          '1974-09-26'),  -- 12
  ('Brian',       'Riemer',      'Dinamarca',      '1975-03-22'),  -- 13
  ('Vincenzo',    'Montella',    'Turquia',        '1974-06-18'),  -- 14
  ('Dragan',      'Stojkovic',   'Serbia',         '1965-03-03'),  -- 15
  ('Lionel',      'Scaloni',     'Argentina',      '1978-05-16'),  -- 16
  ('Dorival',     'Junior',      'Brasil',         '1961-06-03'),  -- 17
  ('Marcelo',     'Bielsa',      'Uruguay',        '1955-07-21'),  -- 18
  ('Nestor',      'Lorenzo',     'Colombia',       '1966-01-12'),  -- 19
  ('Ricardo',     'Gareca',      'Chile',          '1958-02-10'),  -- 20
  ('Sebastian',   'Beccacece',   'Ecuador',        '1980-06-22'),  -- 21
  ('Jesualdo',    'Ferreira',    'Venezuela',      '1947-02-21'),  -- 22
  ('Oscar',       'Villegas',    'Bolivia',        '1974-10-06'),  -- 23
  ('Gustavo',     'Morínigo',    'Paraguay',       '1965-08-14'),  -- 24
  ('Mauricio',    'Pochettino',  'Estados Unidos', '1972-03-02'),  -- 25
  ('Jesse',       'Marsch',      'Canada',         '1974-11-14'),  -- 26
  ('Alexander',   'Lozano',      'Costa Rica',     '1970-05-11'),  -- 27
  ('Thomas',      'Christiansen','Panama',         '1970-11-04'),  -- 28
  ('Walid',       'Regragui',    'Marruecos',      '1975-12-25'),  -- 29
  ('Aliou',       'Cisse',       'Senegal',        '1976-03-24'),  -- 30
  ('Eric',        'Chelle',      'Nigeria',        '1977-04-19'),  -- 31
  ('Otto',        'Addo',        'Ghana',          '1975-09-09'),  -- 32
  ('Marc',        'Brys',        'Camerun',        '1965-01-10'),  -- 33
  ('Emerse',      'Fae',         'Costa de Marfil','1982-06-24'),  -- 34
  ('Fouhami',     'Chaabani',    'Tunez',          '1974-09-17'),  -- 35
  ('Hossam',      'Hassan',      'Egipto',         '1966-08-10'),  -- 36
  ('Eric',        'Sekou',       'Mali',           '1969-03-07'),  -- 37
  ('Hajime',      'Moriyasu',    'Japon',          '1968-08-23'),  -- 38
  ('Hong',        'Myung-bo',    'Corea del Sur',  '1969-02-12'),  -- 39
  ('Herve',       'Renard',      'Arabia Saudita', '1969-09-30'),  -- 40
  ('Amir',        'Ghalenoei',   'Iran',           '1966-03-05'),  -- 41
  ('Tony',        'Popovic',     'Australia',      '1973-07-04'),  -- 42
  ('Branko',      'Ivankovic',   'China',          '1954-02-26'),  -- 43
  ('Darko',       'Dusanovic',   'Nueva Zelanda',  '1963-11-01'),  -- 44
  ('Hernán',      'Lozano',      'Mexico',         '1971-06-12'),  -- 45
  ('Obinna',      'Nwaneri',     'Jamaica',        '1978-03-18'),  -- 46
  ('Juan',        'Pinto',       'Honduras',       '1969-04-22'),  -- 47
  ('Mario',       'Bouzas',      'Guatemala',      '1972-05-10');  -- 48

-- ============================================================
-- EQUIPOS (48 equipos del Mundial 2026)
-- idPais referencia a la tabla Pais, idConfederacion a Confederacion
-- UEFA=1, CONMEBOL=2, CONCACAF=3, CAF=4, AFC=5, OFC=6
-- ============================================================
INSERT INTO Equipo (nombre, rankingFifa, idPais, idConfederacion, idDt) VALUES
  -- UEFA (13 cupos)
  ('Alemania',        13, 4,  1,  1),   -- 1
  ('Francia',          2, 5,  1,  2),   -- 2
  ('España',           3, 6,  1,  3),   -- 3
  ('Portugal',          6, 7,  1,  4),   -- 4
  ('Inglaterra',        5, 8,  1,  5),   -- 5
  ('Holanda',          7, 9,  1,  6),   -- 6
  ('Belgica',         14, 10, 1,  7),   -- 7
  ('Italia',           9, 11, 1,  8),   -- 8
  ('Croacia',         10, 12, 1,  9),   -- 9
  ('Austria',         25, 13, 1, 10),   -- 10
  ('Escocia',         37, 14, 1, 11),   -- 11
  ('Suiza',           18, 15, 1, 12),   -- 12
  ('Dinamarca',       21, 16, 1, 13),   -- 13
  -- CONMEBOL (6 cupos)
  ('Argentina',        1, 19, 2, 16),   -- 14
  ('Brasil',           4, 20, 2, 17),   -- 15
  ('Uruguay',         15, 21, 2, 18),   -- 16
  ('Colombia',        11, 22, 2, 19),   -- 17
  ('Ecuador',         38, 24, 2, 21),   -- 18
  ('Venezuela',       47, 25, 2, 22),   -- 19
  -- CONCACAF (6 cupos)
  ('Estados Unidos',  16, 2,  3, 25),   -- 20
  ('Mexico',          12, 1,  3, 45),   -- 21
  ('Canada',          40, 3,  3, 26),   -- 22
  ('Costa Rica',      53, 28, 3, 27),   -- 23
  ('Panama',          60, 29, 3, 28),   -- 24
  ('Jamaica',         55, 30, 3, 46),   -- 25
  -- CAF (9 cupos)
  ('Marruecos',        14, 34, 4, 29),  -- 26
  ('Senegal',          22, 35, 4, 30),  -- 27
  ('Nigeria',          35, 36, 4, 31),  -- 28
  ('Ghana',            60, 37, 4, 32),  -- 29
  ('Camerun',          42, 38, 4, 33),  -- 30
  ('Costa de Marfil',  48, 39, 4, 34),  -- 31
  ('Tunez',            36, 40, 4, 35),  -- 32
  ('Egipto',           34, 41, 4, 36),  -- 33
  ('Mali',             56, 42, 4, 37),  -- 34
  -- AFC (8 cupos)
  ('Japon',            23, 43, 5, 38),  -- 35
  ('Corea del Sur',    22, 44, 5, 39),  -- 36
  ('Arabia Saudita',   56, 45, 5, 40),  -- 37
  ('Iran',             22, 46, 5, 41),  -- 38
  ('Australia',        24, 47, 5, 42),  -- 39
  ('China',            88, 48, 5, 43),  -- 40
  -- Turquía, Serbia (UEFA restantes)
  ('Turquia',          29, 17, 1, 14),  -- 41
  ('Serbia',           33, 18, 1, 15),  -- 42
  -- Bolivia, Chile, Paraguay (CONMEBOL)
  ('Bolivia',          82, 26, 2, 23),  -- 43
  ('Chile',            27, 23, 2, 20),  -- 44
  ('Paraguay',         67, 27, 2, 24),  -- 45
  -- Honduras, Guatemala (CONCACAF)
  ('Honduras',         75, 31, 3, 47),  -- 46
  ('Guatemala',        95, 32, 3, 48),  -- 47
  -- Nueva Zelanda (OFC)
  ('Nueva Zelanda',   101, 49, 6, 44);  -- 48

-- ============================================================
-- POSICIONES
-- ============================================================
INSERT INTO Posicion (nombre) VALUES
  ('Portero'),       -- 1
  ('Defensa'),       -- 2
  ('Centrocampista'),-- 3
  ('Delantero');     -- 4

-- ============================================================
-- JUGADORES ESTRELLA (muestra representativa por equipo)
-- Se insertan al menos 5 jugadores por equipo para que las
-- consultas de sub21, valor de mercado, etc. funcionen.
-- ============================================================
INSERT INTO Jugador (nombre, apellido, fechaNacimiento, estatura, peso, valorMercado, idEquipo, idPosicion) VALUES
-- Argentina (14)
('Lionel',    'Messi',      '1987-06-24', 1.70, 67.0, 30.00,  14, 4),
('Julian',    'Alvarez',    '2000-01-31', 1.70, 67.0, 90.00,  14, 4),
('Rodrigo',   'De Paul',    '1994-05-24', 1.80, 72.0, 55.00,  14, 3),
('Cristian',  'Romero',     '1998-04-27', 1.85, 80.0, 65.00,  14, 2),
('Emiliano',  'Martinez',   '1992-09-02', 1.95, 83.0, 30.00,  14, 1),
-- Brasil (15)
('Vinicius',  'Junior',     '2000-07-12', 1.76, 73.0, 200.00, 15, 4),
('Rodrygo',   'Goes',       '2001-01-09', 1.74, 64.0, 100.00, 15, 4),
('Endrick',   'Felipe',     '2006-07-21', 1.73, 73.0, 60.00,  15, 4),
('Casemiro',  'Lima',       '1992-02-23', 1.85, 78.0, 35.00,  15, 3),
('Alisson',   'Becker',     '1992-10-02', 1.91, 91.0, 30.00,  15, 1),
-- Francia (2)
('Kylian',    'Mbappe',     '1998-12-20', 1.78, 73.0, 180.00,  2, 4),
('Antoine',   'Griezmann',  '1991-03-21', 1.76, 73.0, 30.00,   2, 4),
('Aurelien',  'Tchouameni', '2000-01-27', 1.88, 80.0, 80.00,   2, 3),
('William',   'Saliba',     '2001-03-24', 1.92, 82.0, 80.00,   2, 2),
('Mike',      'Maignan',    '1995-07-03', 1.91, 89.0, 40.00,   2, 1),
-- España (3)
('Pedri',     'Gonzalez',   '2002-11-25', 1.74, 60.0, 120.00,  3, 3),
('Gavi',      'Paez',       '2004-08-05', 1.73, 60.0, 110.00,  3, 3),
('Lamine',    'Yamal',      '2007-07-13', 1.80, 62.0, 180.00,  3, 4),
('Aymeric',   'Laporte',    '1994-05-27', 1.89, 86.0, 30.00,   3, 2),
('Unai',      'Simon',      '1997-06-11', 1.90, 78.0, 30.00,   3, 1),
-- Portugal (4)
('Cristiano', 'Ronaldo',    '1985-02-05', 1.87, 83.0, 15.00,   4, 4),
('Bernardo',  'Silva',      '1994-08-10', 1.73, 64.0, 80.00,   4, 3),
('Rafael',    'Leao',       '1999-06-10', 1.88, 78.0, 90.00,   4, 4),
('Ruben',     'Dias',       '1997-05-14', 1.87, 76.0, 70.00,   4, 2),
('Diogo',     'Costa',      '1999-09-19', 1.85, 79.0, 35.00,   4, 1),
-- Inglaterra (5)
('Jude',      'Bellingham',  '2003-06-29', 1.86, 75.0, 180.00,  5, 3),
('Phil',      'Foden',       '2000-05-28', 1.71, 69.0, 150.00,  5, 3),
('Harry',     'Kane',        '1993-07-28', 1.88, 89.0, 100.00,  5, 4),
('Bukayo',    'Saka',        '2001-09-05', 1.78, 70.0, 150.00,  5, 4),
('Jordan',    'Pickford',    '1994-03-07', 1.85, 76.0, 20.00,   5, 1),
-- Alemania (1)
('Florian',   'Wirtz',       '2003-05-03', 1.76, 70.0, 150.00,  1, 3),
('Jamal',     'Musiala',     '2003-02-26', 1.76, 70.0, 130.00,  1, 3),
('Kai',       'Havertz',     '1999-06-11', 1.89, 76.0,  70.00,  1, 4),
('Antonio',   'Rudiger',     '1993-03-03', 1.90, 85.0,  20.00,  1, 2),
('Manuel',    'Neuer',       '1986-03-27', 1.93, 93.0,  10.00,  1, 1),
-- Marruecos (26)
('Achraf',    'Hakimi',      '1998-11-04', 1.81, 73.0, 70.00,  26, 2),
('Hakim',     'Ziyech',      '1993-03-19', 1.81, 72.0, 15.00,  26, 3),
('Yassine',   'Bounou',      '1991-04-05', 1.92, 90.0, 20.00,  26, 1),
('Sofyan',    'Amrabat',     '1996-08-21', 1.81, 76.0, 20.00,  26, 3),
('Youssef',   'En-Nesyri',   '1997-06-01', 1.89, 78.0, 30.00,  26, 4),
-- Japon (35)
('Takefusa',  'Kubo',        '2001-06-04', 1.73, 67.0, 50.00,  35, 3),
('Ritsu',     'Doan',        '1998-06-16', 1.72, 67.0, 22.00,  35, 3),
('Daichi',    'Kamada',      '1996-08-05', 1.78, 72.0, 28.00,  35, 3),
('Maya',      'Yoshida',     '1988-08-24', 1.89, 82.0,  2.00,  35, 2),
('Shuichi',   'Gonda',       '1989-03-03', 1.85, 83.0,  1.50, 35, 1),
-- Estados Unidos (20)
('Christian', 'Pulisic',     '1998-09-18', 1.77, 70.0, 40.00,  20, 3),
('Gio',       'Reyna',       '2002-11-13', 1.80, 70.0, 35.00,  20, 3),
('Weston',    'McKennie',    '1998-08-28', 1.83, 78.0, 28.00,  20, 3),
('Tyler',     'Adams',       '1999-02-14', 1.78, 72.0, 25.00,  20, 3),
('Matt',      'Turner',      '1994-06-24', 1.93, 88.0,  6.00,  20, 1),
-- Mexico (21)
('Edson',     'Alvarez',     '1997-10-24', 1.86, 78.0, 40.00,  21, 3),
('Santiago',  'Gimenez',     '2001-04-18', 1.86, 78.0, 60.00,  21, 4),
('Hirving',   'Lozano',      '1995-07-30', 1.70, 68.0, 20.00,  21, 4),
('Guillermo', 'Ochoa',       '1985-07-13', 1.83, 82.0,  3.00,  21, 1),
('Cesar',     'Montes',      '1997-03-24', 1.90, 84.0, 10.00,  21, 2),
-- Canada (22)
('Alphonso',  'Davies',      '2000-11-02', 1.80, 76.0, 70.00,  22, 2),
('Jonathan',  'David',       '2000-01-14', 1.80, 68.0, 65.00,  22, 4),
('Tajon',     'Buchanan',    '1999-02-08', 1.82, 72.0, 15.00,  22, 2),
('Cyle',      'Larin',       '1995-04-17', 1.86, 88.0, 10.00,  22, 4),
('Milan',     'Borjan',      '1987-10-23', 1.94, 86.0,  2.00,  22, 1),
-- Colombia (17)
('Luis',      'Diaz',        '1997-01-13', 1.78, 73.0, 85.00,  17, 4),
('James',     'Rodriguez',   '1991-07-12', 1.80, 75.0, 10.00,  17, 3),
('Jhon',      'Cordoba',     '1993-12-11', 1.90, 85.0,  8.00,  17, 4),
('Davinson',  'Sanchez',     '1996-06-12', 1.88, 83.0, 15.00,  17, 2),
('David',     'Ospina',      '1988-08-31', 1.83, 83.0,  1.00,  17, 1),
-- Restantes (un jugador por equipo para que todas las consultas funcionen)
('Cengiz',    'Under',       '1997-07-26', 1.75, 67.0, 14.00, 41, 4),  -- Turquía
('Aleksandar','Mitrovic',    '1994-09-16', 1.89, 84.0, 10.00, 42, 4),  -- Serbia
('Theo',      'Hernandez',   '1997-10-06', 1.84, 75.0, 60.00,  6, 2),  -- Holanda
('Romelu',    'Lukaku',      '1993-05-13', 1.90, 91.0, 15.00,  7, 4),  -- Bélgica
('Gianluca',  'Scamacca',    '1999-01-01', 1.95, 90.0, 35.00,  8, 4),  -- Italia
('Luka',      'Modric',      '1985-09-09', 1.74, 66.0, 10.00,  9, 3),  -- Croacia
('David',     'Alaba',       '1992-06-24', 1.80, 76.0, 10.00, 10, 2),  -- Austria
('Lyndon',    'Dykes',       '1995-10-07', 1.85, 80.0,  4.00, 11, 4),  -- Escocia
('Granit',    'Xhaka',       '1992-09-27', 1.85, 79.0, 20.00, 12, 3),  -- Suiza
('Christian', 'Eriksen',     '1992-02-14', 1.82, 76.0, 10.00, 13, 3),  -- Dinamarca
('Sadio',     'Mane',        '1992-04-10', 1.74, 69.0, 20.00, 27, 4),  -- Senegal
('Victor',    'Osimhen',     '1998-12-29', 1.85, 78.0, 70.00, 28, 4),  -- Nigeria
('Jordan',    'Ayew',        '1991-09-11', 1.79, 74.0,  3.00, 29, 4),  -- Ghana
('Andre',     'Onana',       '1996-04-02', 1.90, 89.0, 20.00, 30, 1),  -- Camerun
('Wilfried',  'Zaha',        '1992-11-10', 1.79, 69.0,  6.00, 31, 4),  -- Costa de Marfil
('Wahbi',     'Khazri',      '1991-02-08', 1.78, 74.0,  3.00, 32, 4),  -- Túnez
('Mohamed',   'Salah',       '1992-06-15', 1.75, 71.0, 30.00, 33, 4),  -- Egipto
('Nene',      'Coulibaly',   '2002-03-15', 1.78, 72.0,  5.00, 34, 3),  -- Mali
('Kaoru',     'Mitoma',      '1997-05-20', 1.76, 71.0, 32.00, 35, 4),  -- Japón
('Son',       'Heung-min',   '1992-07-08', 1.83, 78.0, 25.00, 36, 4),  -- Corea del Sur
('Salem',     'Al-Dawsari',  '1991-08-19', 1.76, 69.0,  4.00, 37, 4),  -- Arabia Saudita
('Sardar',    'Azmoun',      '1995-01-01', 1.87, 80.0,  8.00, 38, 4),  -- Irán
('Mathew',    'Leckie',      '1991-02-04', 1.78, 74.0,  3.00, 39, 4),  -- Australia
('Wu',        'Lei',         '1991-11-19', 1.74, 67.0,  1.50, 40, 4),  -- China
('Marcelo',   'Moreno',      '1987-06-18', 1.87, 83.0,  1.00, 43, 4),  -- Bolivia
('Ben',       'Brereton',    '2000-04-18', 1.91, 81.0,  8.00, 44, 4),  -- Chile
('Miguel',    'Almiron',     '1994-02-10', 1.74, 66.0, 14.00, 45, 3),  -- Paraguay
('Romell',    'Quioto',      '1991-08-15', 1.73, 69.0,  2.00, 46, 4),  -- Honduras
('Rafael',    'Morales',     '1996-12-12', 1.75, 70.0,  1.50, 47, 4),  -- Guatemala
('Chris',     'Wood',        '1991-12-07', 1.91, 84.0,  3.00, 48, 4),  -- Nueva Zelanda
('Morad',     'Amouri',      '2002-09-12', 1.78, 72.0, 10.00, 24, 4),  -- Panamá
('Shamar',    'Nicholson',   '1998-06-28', 1.80, 73.0,  3.00, 25, 4),  -- Jamaica
('Bryan',     'Ruiz',        '1985-08-18', 1.86, 78.0,  1.00, 23, 3);  -- Costa Rica

-- ============================================================
-- GRUPOS (A - L, 12 grupos de 4 equipos)
-- ============================================================
INSERT INTO Grupo (letra) VALUES
  ('A'),('B'),('C'),('D'),('E'),('F'),
  ('G'),('H'),('I'),('J'),('K'),('L');

-- ============================================================
-- EQUIPO-GRUPO (4 equipos por grupo = 48 equipos total)
-- Sorteo hipotético representativo
-- ============================================================
INSERT INTO EquipoGrupo (idGrupo, idEquipo) VALUES
  (1, 1),(1,14),(1,26),(1,35),   -- A: Alemania, Argentina, Marruecos, Japón
  (2, 2),(2,15),(2,20),(2,36),   -- B: Francia, Brasil, USA, Corea del Sur
  (3, 3),(3,16),(3,21),(3,27),   -- C: España, Uruguay, México, Senegal
  (4, 4),(4,17),(4,22),(4,28),   -- D: Portugal, Colombia, Canadá, Nigeria
  (5, 5),(5,18),(5,23),(5,29),   -- E: Inglaterra, Ecuador, Costa Rica, Ghana
  (6, 6),(6,19),(6,24),(6,30),   -- F: Holanda, Venezuela, Panamá, Camerún
  (7, 7),(7,43),(7,25),(7,31),   -- G: Bélgica, Bolivia, Jamaica, Costa de Marfil
  (8, 8),(8,44),(8,46),(8,32),   -- H: Italia, Chile, Honduras, Túnez
  (9, 9),(9,45),(9,47),(9,33),   -- I: Croacia, Paraguay, Guatemala, Egipto
  (10,10),(10,41),(10,48),(10,37), -- J: Austria, Turquía, NZ, Arabia Saudita
  (11,11),(11,42),(11,40),(11,38), -- K: Escocia, Serbia, China, Irán
  (12,12),(12,13),(12,39),(12,34); -- L: Suiza, Dinamarca, Australia, Mali

-- ============================================================
-- PARTIDOS (fase de grupos - 3 partidos por grupo = 36 partidos)
-- Cada grupo tiene sus 3 partidos entre sus 4 equipos (ronda 1)
-- ============================================================
INSERT INTO Partido (horaFecha, idGrupo, idEstadio) VALUES
  -- Grupo A (Estadio Azteca - id 1)
  ('2026-06-11 18:00:00', 1, 1),  -- 1
  ('2026-06-12 21:00:00', 1, 2),  -- 2
  ('2026-06-16 18:00:00', 1, 3),  -- 3
  -- Grupo B (MetLife - id 4)
  ('2026-06-12 15:00:00', 2, 4),  -- 4
  ('2026-06-13 18:00:00', 2, 5),  -- 5
  ('2026-06-17 15:00:00', 2, 6),  -- 6
  -- Grupo C (SoFi - id 5)
  ('2026-06-13 21:00:00', 3, 5),  -- 7
  ('2026-06-14 18:00:00', 3, 1),  -- 8
  ('2026-06-18 21:00:00', 3, 2),  -- 9
  -- Grupo D (AT&T - id 6)
  ('2026-06-14 21:00:00', 4, 6),  -- 10
  ('2026-06-15 18:00:00', 4, 7),  -- 11
  ('2026-06-19 18:00:00', 4, 8),  -- 12
  -- Grupo E (Lumen - id 9)
  ('2026-06-15 21:00:00', 5, 9),  -- 13
  ('2026-06-16 15:00:00', 5, 10), -- 14
  ('2026-06-20 15:00:00', 5, 11), -- 15
  -- Grupo F (BC Place - id 12)
  ('2026-06-16 21:00:00', 6, 12), -- 16
  ('2026-06-17 18:00:00', 6, 1),  -- 17
  ('2026-06-21 18:00:00', 6, 2),  -- 18
  -- Grupo G (BBVA - id 3)
  ('2026-06-17 21:00:00', 7, 3),  -- 19
  ('2026-06-18 15:00:00', 7, 4),  -- 20
  ('2026-06-22 21:00:00', 7, 5),  -- 21
  -- Grupo H (Hard Rock - id 8)
  ('2026-06-18 18:00:00', 8, 8),  -- 22
  ('2026-06-19 21:00:00', 8, 9),  -- 23
  ('2026-06-23 18:00:00', 8, 10), -- 24
  -- Grupo I (Gillette - id 10)
  ('2026-06-19 15:00:00', 9, 10), -- 25
  ('2026-06-20 18:00:00', 9, 11), -- 26
  ('2026-06-24 15:00:00', 9, 12), -- 27
  -- Grupo J (Levi's - id 7)
  ('2026-06-20 21:00:00',10, 7),  -- 28
  ('2026-06-21 15:00:00',10, 8),  -- 29
  ('2026-06-25 21:00:00',10, 9),  -- 30
  -- Grupo K (BMO - id 11)
  ('2026-06-21 18:00:00',11, 11), -- 31
  ('2026-06-22 15:00:00',11, 12), -- 32
  ('2026-06-26 18:00:00',11, 1),  -- 33
  -- Grupo L (Azteca - id 1)
  ('2026-06-22 18:00:00',12, 1),  -- 34
  ('2026-06-23 15:00:00',12, 2),  -- 35
  ('2026-06-27 15:00:00',12, 3);  -- 36

-- ============================================================
-- DETALLE PARTIDOS (local / visitante para los 36 partidos)
-- ============================================================
INSERT INTO DetallePartido (idPartido, idEquipo, condicion) VALUES
  -- Partido 1: Alemania vs Argentina
  (1,  1,  'LOCAL'), (1, 14, 'VISITANTE'),
  -- Partido 2: Marruecos vs Japón
  (2, 26,  'LOCAL'), (2, 35, 'VISITANTE'),
  -- Partido 3: Alemania vs Marruecos
  (3,  1,  'LOCAL'), (3, 26, 'VISITANTE'),
  -- Partido 4: Francia vs Brasil
  (4,  2,  'LOCAL'), (4, 15, 'VISITANTE'),
  -- Partido 5: USA vs Corea del Sur
  (5, 20,  'LOCAL'), (5, 36, 'VISITANTE'),
  -- Partido 6: Francia vs USA
  (6,  2,  'LOCAL'), (6, 20, 'VISITANTE'),
  -- Partido 7: España vs Uruguay
  (7,  3,  'LOCAL'), (7, 16, 'VISITANTE'),
  -- Partido 8: México vs Senegal
  (8, 21,  'LOCAL'), (8, 27, 'VISITANTE'),
  -- Partido 9: España vs México
  (9,  3,  'LOCAL'), (9, 21, 'VISITANTE'),
  -- Partido 10: Portugal vs Colombia
  (10, 4,  'LOCAL'), (10,17, 'VISITANTE'),
  -- Partido 11: Canadá vs Nigeria
  (11,22,  'LOCAL'), (11,28, 'VISITANTE'),
  -- Partido 12: Portugal vs Canadá
  (12, 4,  'LOCAL'), (12,22, 'VISITANTE'),
  -- Partido 13: Inglaterra vs Ecuador
  (13, 5,  'LOCAL'), (13,18, 'VISITANTE'),
  -- Partido 14: Costa Rica vs Ghana
  (14,23,  'LOCAL'), (14,29, 'VISITANTE'),
  -- Partido 15: Inglaterra vs Costa Rica
  (15, 5,  'LOCAL'), (15,23, 'VISITANTE'),
  -- Partido 16: Holanda vs Venezuela
  (16, 6,  'LOCAL'), (16,19, 'VISITANTE'),
  -- Partido 17: Panamá vs Camerún
  (17,24,  'LOCAL'), (17,30, 'VISITANTE'),
  -- Partido 18: Holanda vs Panamá
  (18, 6,  'LOCAL'), (18,24, 'VISITANTE'),
  -- Partido 19: Bélgica vs Bolivia
  (19, 7,  'LOCAL'), (19,43, 'VISITANTE'),
  -- Partido 20: Jamaica vs Costa de Marfil
  (20,25,  'LOCAL'), (20,31, 'VISITANTE'),
  -- Partido 21: Bélgica vs Jamaica
  (21, 7,  'LOCAL'), (21,25, 'VISITANTE'),
  -- Partido 22: Italia vs Chile
  (22, 8,  'LOCAL'), (22,44, 'VISITANTE'),
  -- Partido 23: Honduras vs Túnez
  (23,46,  'LOCAL'), (23,32, 'VISITANTE'),
  -- Partido 24: Italia vs Honduras
  (24, 8,  'LOCAL'), (24,46, 'VISITANTE'),
  -- Partido 25: Croacia vs Paraguay
  (25, 9,  'LOCAL'), (25,45, 'VISITANTE'),
  -- Partido 26: Guatemala vs Egipto
  (26,47,  'LOCAL'), (26,33, 'VISITANTE'),
  -- Partido 27: Croacia vs Guatemala
  (27, 9,  'LOCAL'), (27,47, 'VISITANTE'),
  -- Partido 28: Austria vs Turquía
  (28,10,  'LOCAL'), (28,41, 'VISITANTE'),
  -- Partido 29: Nueva Zelanda vs Arabia Saudita
  (29,48,  'LOCAL'), (29,37, 'VISITANTE'),
  -- Partido 30: Austria vs Nueva Zelanda
  (30,10,  'LOCAL'), (30,48, 'VISITANTE'),
  -- Partido 31: Escocia vs Serbia
  (31,11,  'LOCAL'), (31,42, 'VISITANTE'),
  -- Partido 32: China vs Irán
  (32,40,  'LOCAL'), (32,38, 'VISITANTE'),
  -- Partido 33: Escocia vs China
  (33,11,  'LOCAL'), (33,40, 'VISITANTE'),
  -- Partido 34: Suiza vs Dinamarca
  (34,12,  'LOCAL'), (34,13, 'VISITANTE'),
  -- Partido 35: Australia vs Mali
  (35,39,  'LOCAL'), (35,34, 'VISITANTE'),
  -- Partido 36: Suiza vs Australia
  (36,12,  'LOCAL'), (36,39, 'VISITANTE');

-- ============================================================
-- USUARIO ADMINISTRADOR INICIAL
-- Contraseña: Admin2026! → SHA-256
-- IMPORTANTE: Cambia la contraseña en el primer login.
-- ============================================================
INSERT INTO Usuario (nombreUsuario, contrasena, tipoUsuario)
VALUES (
  'admin',
  '7a1f5f3e9b2c0d4e8f6a3b1c9d2e4f0a8b7c5d3e1f9a2b4c6d8e0f1a3b5c7d9',
  'ADMINISTRADOR'
);

-- Nota: el hash arriba es un placeholder. Para generar el hash real de "Admin2026!" ejecuta:
-- SELECT SHA2('Admin2026!', 256);
-- Y reemplaza el valor en el UPDATE:
UPDATE Usuario
SET contrasena = SHA2('Admin2026!', 256)
WHERE nombreUsuario = 'admin';