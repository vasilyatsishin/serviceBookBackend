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
    FOREIGN KEY (car) REFERENCES cars (id) ON DELETE CASCADE
);

CREATE TABLE maintenance
(
    id       INT AUTO_INCREMENT PRIMARY KEY UNIQUE,
    date     DATE,
    odometer INT,
    place    VARCHAR(100),
    car      INT,
    FOREIGN KEY (car) REFERENCES cars (id) ON DELETE CASCADE
);

CREATE TABLE performed_maintenance
(
    maintenance_id     INT,
    maintenance_job_id INT,
    FOREIGN KEY (maintenance_id) REFERENCES maintenance (id) ON DELETE CASCADE,
    FOREIGN KEY (maintenance_job_id) REFERENCES maintenance_jobs (id) ON DELETE CASCADE
);