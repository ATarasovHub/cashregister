package com.pos.cashregister.controller;

import com.pos.cashregister.model.Receipt;
import com.pos.cashregister.model.ReceiptItem;
import com.pos.cashregister.service.ReceiptService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReceiptControllerTest {
    @Mock
    private ReceiptService service;
    @Mock
    private Model model;
    @InjectMocks
    private ReceiptController controller;

    @Test
    void getHomeProducktPageTest() {
        String viewName = controller.getReceiptsPage(model);

        verify(model).addAttribute(eq("receipts"), anyList());
        assert viewName.equals("receipts");
    }

    @Test
    void getAllReceiptsShouldReturnListOfProductsWhenProductsExist() {
        List<Receipt> mocReceipts = List.of(
                Receipt.builder()
                        .id(1L)
                        .build(),
                Receipt.builder()
                        .id(2L)
                        .build()
        );

        when(service.getAllReceipts()).thenReturn(mocReceipts);

        ResponseEntity<List<Receipt>> response = controller.getAllReceipts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getReceiptByIdShouldReturnOkWithProductDetails() {
        Long receiptId = 1L;
        Receipt mockReceipt = new Receipt();
        mockReceipt.setId(receiptId);

        when(service.getReceiptById(receiptId)).thenReturn(Optional.of(mockReceipt));

        ResponseEntity<Receipt> response = controller.getReceiptById(receiptId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockReceipt, response.getBody());
        verify(service).getReceiptById(receiptId);
    }

    @Test
    void shouldReturnNotFoundWhenReceiptDoesNotExist() {
        Long receiptId = 1L;

        when(service.getReceiptById(receiptId)).thenReturn(Optional.empty());

        ResponseEntity<Receipt> response = controller.getReceiptById(receiptId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(service).getReceiptById(receiptId);
    }

    @Test
    void shouldCreateReceiptWhenValidData() {
        Receipt mockReceipt = new Receipt();

        ReceiptItem item = new ReceiptItem();
        mockReceipt.setItems(List.of(item));

        when(service.createReceipt(mockReceipt)).thenReturn(mockReceipt);

        ResponseEntity<?> response = controller.createReceipt(mockReceipt);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockReceipt, response.getBody());

        verify(service).createReceipt(mockReceipt);
    }

    @Test
    void shouldReturnBadRequestWhenErrorCreatingReceipt() {
        Receipt mockReceipt = new Receipt();

        ReceiptItem item = new ReceiptItem();
        mockReceipt.setItems(List.of(item));

        when(service.createReceipt(mockReceipt)).thenThrow(new RuntimeException("Creation failed"));

        ResponseEntity<?> response = controller.createReceipt(mockReceipt);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).toString().contains("Error creating receipt"));

        verify(service).createReceipt(mockReceipt);
    }

    @Test
    void shouldDeleteReceiptWhenExists() {
        Long receiptId = 1L;
        Receipt mockReceipt = new Receipt();
        mockReceipt.setId(receiptId);

        when(service.getReceiptById(receiptId)).thenReturn(Optional.of(mockReceipt));
        doNothing().when(service).deleteReceipt(receiptId);

        ResponseEntity<Void> response = controller.deleteReceipt(receiptId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).deleteReceipt(receiptId);
    }

    @Test
    void shouldReturnNotFoundWhenReceiptDoesNotExistTest() {
        Long receiptId = 1L;

        when(service.getReceiptById(receiptId)).thenReturn(Optional.empty());

        ResponseEntity<Void> response = controller.deleteReceipt(receiptId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(service, never()).deleteReceipt(receiptId);
    }
}
