package com.pos.cashregister.controller;

import com.pos.cashregister.model.Receipt;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReceiptControllerTest {
    @Mock private ReceiptService service;
    @Mock private Model model;
    @InjectMocks private ReceiptController controller;

    @Test
    void getHomeProducktPageTest(){
        String viewName = controller.getReceiptsPage(model);

        verify(model).addAttribute(eq("receipts"), anyList());
        assert viewName.equals("receipts");
    }

    @Test
    void getAllReceiptsShouldReturnListOfProductsWhenProductsExist(){
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

}
