package com.pos.cashregister.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Column(nullable = false)
    private String cashierName;

    @Column(nullable = false)
    private String paymentMethod;

    @Column(name = "payment_received")
    private BigDecimal paymentReceived;

    @Column(name = "change_amount")
    private BigDecimal changeAmount;
}
