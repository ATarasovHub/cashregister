package com.pos.cashregister.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiptItemDTO {
    private Long productId;
    private int quantity;
}
