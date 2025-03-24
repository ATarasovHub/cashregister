package com.pos.cashregister.service;

import com.pos.cashregister.model.Product;
import com.pos.cashregister.model.ProductType;
import com.pos.cashregister.model.Receipt;
import com.pos.cashregister.model.ReceiptItem;
import com.pos.cashregister.repository.JpaReceiptRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ReceiptServiceTest {

    @InjectMocks
    private ReceiptService service;
    @Mock
    private JpaReceiptRepository repository;
    @Mock
    private ProductService productService;
    @Mock
    private JpaReceiptRepository receiptRepository;

    @Test
    void shouldReturnListOfReceipts() {
        List<Receipt> receipts = List.of(
                Receipt.builder()
                        .id(1L)
                        .build(),

                Receipt.builder()
                        .id(2L)
                        .build()
        );

        when(repository.findAll()).thenReturn(receipts);

        List<Receipt> result = service.getAllReceipts();

        assertEquals(2, result.size());
        assertEquals(1L,result.getFirst().getId());
        assertEquals(2L,result.get(1).getId());

        verify(repository).findAll() ;
    }

    @Test
    void shouldReturnReceiptsByIdWhenReceiptExist() {
        Long receiptId = 1L;
        Receipt receipt = new Receipt();
        receipt.setId(receiptId);

        when(repository.findById(receiptId)).thenReturn(Optional.of(receipt));

        Optional<Receipt> result = service.getReceiptById(receiptId);

        assertTrue(result.isPresent());
        assertEquals(receipt, result.get());
        verify(repository).findById(receiptId);
    }

    @Test
    void shouldDeleteReceiptSuccessfully() {
        Long receiptId = 1L;

        doNothing().when(repository).deleteById(receiptId);

        service.deleteReceipt(receiptId);

        verify(repository).deleteById(receiptId);
    }

    @Test
    void createReceipt_shouldCalculateTotalsAndSaveReceipt() {
        Product product = Product.builder()
                .id(1L)
                .name("Milk")
                .price(new BigDecimal("2.00"))
                .productType(ProductType.ESSENTIAL)
                .stockQuantity(10)
                .build();

        ReceiptItem item = ReceiptItem.builder()
                .productId(1L)
                .quantity(2)
                .build();

        Receipt incomingReceipt = Receipt.builder()
                .cashierName("John")
                .paymentMethod("Cash")
                .items(List.of(item))
                .paymentReceived(new BigDecimal("5.00"))
                .changeAmount(new BigDecimal("0.00"))
                .build();

        when(productService.getProductById(1L)).thenReturn(Optional.of(product));
        when(receiptRepository.save(any(Receipt.class))).thenAnswer(i -> i.getArguments()[0]);

        Receipt saved = service.createReceipt(incomingReceipt);

        assertEquals("John", saved.getCashierName());
        assertEquals("Cash", saved.getPaymentMethod());
        assertEquals(1, saved.getItems().size());

        ReceiptItem savedItem = saved.getItems().get(0);
        assertEquals("Milk", savedItem.getProductName());
        assertEquals(new BigDecimal("2.00"), savedItem.getPrice());
        assertEquals(2, savedItem.getQuantity());

        BigDecimal expectedSubtotal = new BigDecimal("4.00");
        BigDecimal expectedVat = expectedSubtotal.multiply(new BigDecimal("0.07")).setScale(2);
        BigDecimal expectedTotal = expectedSubtotal.add(expectedVat).setScale(2);

        assertEquals(expectedVat, saved.getTaxAmount());
        assertEquals(expectedTotal, saved.getTotal());
        verify(productService, times(1)).updateStock(1L, 2);
        verify(receiptRepository, times(1)).save(any(Receipt.class));
    }
}
