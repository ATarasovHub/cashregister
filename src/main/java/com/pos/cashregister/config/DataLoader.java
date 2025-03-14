package com.pos.cashregister.config;

import com.pos.cashregister.model.Product;

import com.pos.cashregister.repository.JpaProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final JpaProductRepository productRepository;

    @Autowired
    public DataLoader(JpaProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {

        if (productRepository.count() == 0) {
            loadSampleProducts();
        }
    }

    private void loadSampleProducts() {
        List<Product> products = List.of(
                new Product("Coffee", "Freshly brewed coffee", new BigDecimal("2.50"), "Beverages", "BVRG001", 100),
                new Product("Tea", "Assorted tea varieties", new BigDecimal("2.00"), "Beverages", "BVRG002", 80),
                new Product("Croissant", "Butter croissant", new BigDecimal("3.50"), "Bakery", "BKY001", 30),
                new Product("Bagel", "Plain bagel", new BigDecimal("2.75"), "Bakery", "BKY002", 25),
                new Product("Sandwich", "Ham and cheese sandwich", new BigDecimal("5.00"), "Food", "FOOD001", 15),
                new Product("Salad", "Fresh garden salad", new BigDecimal("6.50"), "Food", "FOOD002", 10),
                new Product("Muffin", "Blueberry muffin", new BigDecimal("3.25"), "Bakery", "BKY003", 20),
                new Product("Soda", "Assorted sodas", new BigDecimal("1.75"), "Beverages", "BVRG003", 50),
                new Product("Water", "Bottled water", new BigDecimal("1.50"), "Beverages", "BVRG004", 60),
                new Product("Cookie", "Chocolate chip cookie", new BigDecimal("1.75"), "Bakery", "BKY004", 40)
        );

        productRepository.saveAll(products);

        System.out.println("Sample products loaded into database");
    }
}
