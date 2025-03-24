package com.pos.cashregister.service;

import com.pos.cashregister.model.Receipt;
import com.pos.cashregister.repository.JpaReceiptRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
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
    void shouldReturnReceiptsWithinDateRange() {
        Receipt receipt1 = new Receipt();
        receipt1.setDateTime(LocalDateTime.of(2025,3,1,12,0,0));

        Receipt receipt2 = new Receipt();
        receipt2.setDateTime(LocalDateTime.of(2025,3,10,12,0,0));

        Receipt receipt3 = new Receipt();
        receipt3.setDateTime(LocalDateTime.of(2025,3,20,12,0,0));

        List<Receipt> receipts = Arrays.asList(receipt1, receipt2, receipt3);

        when(repository.findAll()).thenReturn(receipts);

        String startDate = "2025-03-05";
        String endDate = "2025-03-15";

        List<Receipt> result = service.searchReceiptsByDateRange(startDate, endDate);

        assertEquals(1, result.size());
        assertEquals(receipt2, result.get(0));

        verify(repository).findAll();
    }
}
