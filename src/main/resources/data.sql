
INSERT INTO products (name,description,price, category,barcode,stock_quantity, product_type)
VALUES
    ('Apple', 'Fresh Red Apple', 1.99, 'Fruits', '123456', 50, 'ESSENTIAL'),
    ('Banana', 'Yellow Banana', 0.99, 'Fruits', '654321', 100, 'ESSENTIAL'),
    ('Milk', '1L Whole Milk', 2.49, 'Dairy', '111222', 30, 'ESSENTIAL'),
    ('Bread', 'Whole Wheat Bread', 2.99, 'Bakery', '333444', 20, 'CONSUMER'),
    ('Eggs', 'Pack of 12 Eggs', 3.99, 'Dairy', '555666', 40, 'CONSUMER');


INSERT INTO receipts (date_time, total, tax_amount, cashier_name, payment_method, payment_received, change_amount)
VALUES
    ('2025-03-14 12:00:00', 10.00, 1.00, 'John Doe', 'Cash', 12.00, 2.00),
    ('2025-03-14 12:30:00', 15.50, 1.55, 'Jane Smith', 'Credit Card', 17.00, 1.50),
    ('2025-03-14 13:00:00', 20.75, 2.07, 'Alice Johnson', 'Mobile Payment', 22.00, 1.25);


INSERT INTO receipt_items (receipt_id, product_id, product_name, price, quantity, total)
VALUES
    (1, 1, 'Apple', 1.99, 2, 3.98),
    (1, 3, 'Milk', 2.49, 2, 4.98),
    (2, 2, 'Banana', 0.99, 5, 4.95),
    (2, 4, 'Bread', 2.99, 3, 8.97),
    (3, 5, 'Eggs', 3.99, 2, 7.98);
