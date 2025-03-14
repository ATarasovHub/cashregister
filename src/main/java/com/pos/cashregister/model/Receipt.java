package com.pos.cashregister.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "receipts")
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "receipt_id")
    private List<ReceiptItem> items;

    @Column(nullable = false)
    private BigDecimal total;

    @Column(nullable = false)
    private BigDecimal taxAmount;

    private String cashierName;

    private String paymentMethod;

    public void calculateTotal() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (ReceiptItem item : items) {
            subtotal = subtotal.add(item.getTotal());
        }

        // Assuming tax rate of 10% for example
        this.taxAmount = subtotal.multiply(new BigDecimal("0.10")).setScale(2, BigDecimal.ROUND_HALF_UP);
        this.total = subtotal.add(taxAmount).setScale(2, BigDecimal.ROUND_HALF_UP);
    }


}

