CREATE TABLE users
(
    id       INT AUTO_INCREMENT PRIMARY KEY UNIQUE,
    password VARCHAR(255),
    email    VARCHAR(100) UNIQUE,
    name     VARCHAR(100)
);

ALTER TABLE cars
    ADD user_id INT,
    ADD CONSTRAINT fk_cars_user
        FOREIGN KEY (user_id)
            REFERENCES users (id);

