package com.pos.cashregister.model;


import jakarta.persistence.*;
import java.math.BigDecimal;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "receipt_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReceiptItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private BigDecimal total;

    private void recalculateTotal() {
        if (price != null) {
            this.total = price.multiply(BigDecimal.valueOf(quantity)).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
    }
}
