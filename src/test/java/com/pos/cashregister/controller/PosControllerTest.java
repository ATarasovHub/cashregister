package com.pos.cashregister.controller;

import com.pos.cashregister.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PosControllerTest {

    @Mock private ProductService productService;
    @Mock private Model model;
    @InjectMocks private PosController posController;

    @BeforeEach
    void setup() {
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());
    }

    @Test
    void getHomePageShouldReturnIndexViewWithProducts() {
        String viewName = posController.getHomePage(model);

        verify(model).addAttribute(eq("products"), anyList());
        assert viewName.equals("index");
    }
}
