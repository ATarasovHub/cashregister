package com.pos.cashregister.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiptDTO {
    private String cashierName;
    private String paymentMethod;
    private List<ReceiptItemDTO> items;
}
