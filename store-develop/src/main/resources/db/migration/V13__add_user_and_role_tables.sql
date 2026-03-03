-- Create roles table
CREATE TABLE roles (
    id VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL UNIQUE,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

-- Create users table
CREATE TABLE users (
    id VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

-- Create user_roles join table
CREATE TABLE user_roles (
    user_id VARCHAR(255) NOT NULL,
    role_id VARCHAR(255) NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_on_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_roles_on_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

-- Insert default roles
INSERT INTO roles (id, name) VALUES ('user_role_id', 'ROLE_USER');
INSERT INTO roles (id, name) VALUES ('admin_role_id', 'ROLE_ADMIN');
