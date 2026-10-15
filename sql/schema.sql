-- Online Pet Adoption Platform : schema + seed data (MySQL 5.7+ / 8.x)
CREATE DATABASE IF NOT EXISTS pet_adoption CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pet_adoption;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS applications;
DROP TABLE IF EXISTS pets;
DROP TABLE IF EXISTS settings;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100)  NOT NULL,
    email         VARCHAR(150)  NOT NULL,
    password_hash VARCHAR(255)  NOT NULL,
    role          ENUM('ADMIN','SHELTER','ADOPTER') NOT NULL,
    phone         VARCHAR(20)   NULL,
    address       VARCHAR(255)  NULL,
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_users_email UNIQUE (email),
    INDEX idx_users_role (role)
) ENGINE=InnoDB;

CREATE TABLE pets (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    shelter_id      BIGINT        NOT NULL,
    name            VARCHAR(100)  NOT NULL,
    type            VARCHAR(30)   NOT NULL,
    breed           VARCHAR(100)  NOT NULL,
    age_years       DOUBLE        NOT NULL DEFAULT 0,
    location        VARCHAR(120)  NOT NULL,
    description     TEXT          NULL,
    photo_path      VARCHAR(255)  NULL,
    adoption_status ENUM('AVAILABLE','ADOPTED') NOT NULL DEFAULT 'AVAILABLE',
    approval_status ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pets_shelter FOREIGN KEY (shelter_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_pets_search (approval_status, adoption_status, type),
    INDEX idx_pets_shelter (shelter_id)
) ENGINE=InnoDB;

CREATE TABLE applications (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    pet_id         BIGINT       NOT NULL,
    adopter_id     BIGINT       NOT NULL,
    reason         TEXT         NOT NULL,
    housing_type   VARCHAR(40)  NOT NULL,
    has_other_pets TINYINT(1)   NOT NULL DEFAULT 0,
    experience     VARCHAR(255) NULL,
    status         ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
    shelter_note   VARCHAR(500) NULL,
    applied_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_app_pet     FOREIGN KEY (pet_id)     REFERENCES pets(id)  ON DELETE CASCADE,
    CONSTRAINT fk_app_adopter FOREIGN KEY (adopter_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_app_pet_adopter UNIQUE (pet_id, adopter_id),
    INDEX idx_app_adopter (adopter_id),
    INDEX idx_app_status (status)
) ENGINE=InnoDB;

CREATE TABLE messages (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id   BIGINT        NOT NULL,
    receiver_id BIGINT        NOT NULL,
    content     VARCHAR(2000) NOT NULL,
    is_read     TINYINT(1)    NOT NULL DEFAULT 0,
    sent_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_msg_sender   FOREIGN KEY (sender_id)   REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_msg_receiver FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_msg_receiver (receiver_id, is_read)
) ENGINE=InnoDB;

CREATE TABLE settings (
    setting_key   VARCHAR(60)  PRIMARY KEY,
    setting_value VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

-- ---------------------------------------------------------------- seed data
-- Passwords:  admin@petadopt.com / Admin@123   shelter@petadopt.com / Shelter@123
--             adopter@petadopt.com / Adopter@123   (PBKDF2-HMAC-SHA256, 65536 iterations)
INSERT INTO users (id, name, email, password_hash, role, phone, address) VALUES
(1, 'Platform Admin',     'admin@petadopt.com',   '65536$cGV0YWRvcHQtYWRtaW4tMDE=$l4agfav/1vuXGXHbKH0qDmfQk3Xai0MY2zNlQy9/cYA=',   'ADMIN',   '9000000001', 'Head Office, New Delhi'),
(2, 'Happy Paws Shelter', 'shelter@petadopt.com', '65536$cGV0YWRvcHQtc2hlbHQtMDE=$Fk1dvJMrxQOxnXNf1numIu5JnOjD5VKHD8Ts6qAMJ1A=', 'SHELTER', '9000000002', 'Sector 62, Noida'),
(3, 'Riya Sharma',        'adopter@petadopt.com', '65536$cGV0YWRvcHQtYWRvcHQtMDE=$K08J3Xxe8wy7n3NXirH0g/yyacnDEEzrUu1q7xMheCs=', 'ADOPTER', '9000000003', 'Indirapuram, Ghaziabad'),
(4, 'Tail Waggers Rescue','rescue@petadopt.com',  '65536$cGV0YWRvcHQtc2hlbHQtMDE=$Fk1dvJMrxQOxnXNf1numIu5JnOjD5VKHD8Ts6qAMJ1A=', 'SHELTER', '9000000004', 'Saket, New Delhi');

INSERT INTO pets (shelter_id, name, type, breed, age_years, location, description, adoption_status, approval_status) VALUES
(2, 'Bruno',  'Dog',    'Labrador Retriever', 2,   'Noida',      'Friendly, vaccinated and great with kids. Loves fetch and long walks.', 'AVAILABLE', 'APPROVED'),
(2, 'Misty',  'Cat',    'Persian',            1.5, 'Noida',      'Calm lap cat, litter trained. Prefers a quiet indoor home.',            'AVAILABLE', 'APPROVED'),
(2, 'Coco',   'Rabbit', 'Holland Lop',        0.8, 'Noida',      'Gentle and curious. Comes with a starter cage.',                         'AVAILABLE', 'APPROVED'),
(4, 'Rocky',  'Dog',    'Indian Pariah',      3,   'New Delhi',  'Healthy, street-smart and loyal. Sterilised and vaccinated.',            'AVAILABLE', 'APPROVED'),
(4, 'Luna',   'Cat',    'Indian Shorthair',   0.5, 'New Delhi',  'Playful kitten looking for an active family.',                           'AVAILABLE', 'PENDING'),
(4, 'Sheru',  'Dog',    'German Shepherd',    4,   'New Delhi',  'Well trained, needs space to run.',                                      'ADOPTED',   'APPROVED');

INSERT INTO settings (setting_key, setting_value) VALUES
('site_name', 'Paws & Homes'),
('contact_email', 'support@petadopt.com'),
('allow_registration', 'true'),
('max_active_applications', '3');
