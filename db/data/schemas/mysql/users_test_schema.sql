CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
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

LOAD DATA INFILE '/data/users.csv'
    INTO TABLE users
    FIELDS TERMINATED BY ','
    LINES TERMINATED BY '\n'
    IGNORE 1 LINES
    (id, first_name, last_name, email_address, phone_number, @date_of_birth, address_1, @address_2, city, state, postal_code, created_at, @updated_at)
    SET
        date_of_birth = NULLIF(@date_of_birth, ''),
        address_2 = NULLIF(@address_2, ''),
        updated_at = NULLIF(@updated_at, '');