package com.pos.cashregister.model;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@Getter
@Setter

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    private String category;

    private String barcode;

    private int stockQuantity;


    public Product(String name, String description, BigDecimal price, String category, String barcode, int stockQuantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.barcode = barcode;
        this.stockQuantity = stockQuantity;
    }
}
