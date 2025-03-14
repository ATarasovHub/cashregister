
CREATE TABLE IF NOT EXISTS product (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(255),
    sku VARCHAR(50),
    stock_quantity INT
    );
