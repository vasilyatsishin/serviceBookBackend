CREATE TABLE user_tokens
(
    id       INT AUTO_INCREMENT PRIMARY KEY UNIQUE,
    user_id INT ,
    token    VARCHAR(512) UNIQUE NOT NULL,
    expiry_date    DATETIME,
    FOREIGN KEY (user_id) REFERENCES railway.users (id) ON DELETE CASCADE
);

CREATE INDEX idx_user_token ON user_tokens(user_id);