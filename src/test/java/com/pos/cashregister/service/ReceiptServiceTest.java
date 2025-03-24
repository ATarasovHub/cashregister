package com.pos.cashregister.service;

import com.pos.cashregister.model.Receipt;
import com.pos.cashregister.repository.JpaReceiptRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


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
}
