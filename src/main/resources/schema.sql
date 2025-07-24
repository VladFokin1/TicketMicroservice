DROP TABLE IF EXISTS tickets;
DROP TABLE IF EXISTS routes;
DROP TABLE IF EXISTS carriers;
DROP TABLE IF EXISTS refresh_tokens;
DROP TABLE IF EXISTS users;


CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    login VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL default 'ROLE_CUSTOMER'
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS carriers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS routes (
    id SERIAL PRIMARY KEY,
    departure_point VARCHAR(100) NOT NULL,
    destination_point VARCHAR(100) NOT NULL,
    carrier_id INT NOT NULL REFERENCES carriers(id),
    duration_minutes INT NOT NULL
);

CREATE TABLE IF NOT EXISTS tickets (
    id SERIAL PRIMARY KEY,
    route_id INT NOT NULL REFERENCES routes(id),
    date_time TIMESTAMP NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    user_id INT REFERENCES users(id)
);

-- Индексы для ускорения поиска
CREATE INDEX idx_tickets_user_id ON tickets(user_id);
CREATE INDEX idx_tickets_datetime ON tickets(date_time);
CREATE INDEX idx_routes_points ON routes(departure_point, destination_point);