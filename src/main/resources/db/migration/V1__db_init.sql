CREATE TABLE cars
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    odometer INT,
    name     VARCHAR(100),
    photo    LONGBLOB
);

CREATE TABLE maintenance_jobs
(
    id        INT AUTO_INCREMENT PRIMARY KEY UNIQUE,
    frequency INT,
    name      VARCHAR(100),
    car       INT,
    is_regular BOOLEAN,
    FOREIGN KEY (car) REFERENCES cars (id) ON DELETE CASCADE
);

CREATE TABLE maintenance
(
    id       INT AUTO_INCREMENT PRIMARY KEY UNIQUE,
    date     DATE,
    odometer INT,
    place    VARCHAR(100),
    price    DECIMAL(10, 2),
    car      INT,
    comment  VARCHAR(100),
    FOREIGN KEY (car) REFERENCES cars (id) ON DELETE CASCADE
);

CREATE TABLE performed_maintenance
(
    id                 INT AUTO_INCREMENT PRIMARY KEY UNIQUE,
    maintenance_id     INT,
    maintenance_job_id INT,
    price              DECIMAL(10, 2),
    FOREIGN KEY (maintenance_id) REFERENCES maintenance (id) ON DELETE CASCADE,
    FOREIGN KEY (maintenance_job_id) REFERENCES maintenance_jobs (id) ON DELETE CASCADE
);