CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  first_name VARCHAR(50),
  last_name VARCHAR(50),
  email_address VARCHAR(255),
  phone_number VARCHAR(50),
  date_of_birth DATE,
  address_1 VARCHAR(255),
  address_2 VARCHAR(100),
  city VARCHAR(100),
  state VARCHAR(50),
  postal_code VARCHAR(10),
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

COPY users (id, first_name, last_name, email_address, phone_number, date_of_birth, address_1, address_2, city, state, postal_code, created_at, updated_at)
FROM '/data/users.csv'
WITH (FORMAT CSV, HEADER TRUE);