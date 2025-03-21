package com.pos.cashregister.controller;

import com.pos.cashregister.model.Product;
import com.pos.cashregister.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Mock private ProductService productService;
    @Mock private Model model;
    @InjectMocks private ProductController productController;

    @Test
    void getHomeProducktPageTest(){
        String viewName = productController.getProductsPage(model);

        verify(model).addAttribute(eq("products"), anyList());
        assert viewName.equals("products");
    }

    @Test
    void getAllProductsShouldReturnListOfProductsWhenProductsExist() {
        List<Product> mockProducts = List.of(
                Product.builder()
                        .id(1L)
                        .name("Product 1")
                        .description("Description 1")
                        .price(BigDecimal.valueOf(10.99))
                        .category("Category 1")
                        .barcode("123456789")
                        .stockQuantity(100)
                        .build(),
                Product.builder()
                        .id(2L)
                        .name("Product 2")
                        .description("Description 2")
                        .price(BigDecimal.valueOf(20.99))
                        .category("Category 2")
                        .barcode("987654321")
                        .stockQuantity(200)
                        .build()
        );
        when(productService.getAllProducts()).thenReturn(mockProducts);

        ResponseEntity<List<Product>> response = productController.getAllProducts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }
    @Test
    void getProductByIdShouldReturnOkWithProductDetails() {
        Long productId = 1L;
        Product mockProduct = new Product();
        mockProduct.setId(productId);
        mockProduct.setName("Test Product");

        when(productService.getProductById(productId)).thenReturn(Optional.of(mockProduct));

        ResponseEntity<Product> response = productController.getProductById(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockProduct, response.getBody());
        verify(productService).getProductById(productId);
    }

    @Test
    void shouldCreateProductTest() {
        Product mockProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(10.99))
                .category("Test Category")
                .barcode("123456789")
                .stockQuantity(50)
                .build();

        when(productService.saveProduct(any(Product.class))).thenReturn(mockProduct);

        ResponseEntity<Product> response = productController.createProduct(mockProduct);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockProduct, response.getBody());

        verify(productService).saveProduct(mockProduct);
    }

    @Test
    void shouldUpdateProductSuccessfullyTest() {
        Long productId = 1L;
        Product existingProduct = Product.builder()
                .id(productId)
                .name("Old Name")
                .description("Old Description")
                .price(BigDecimal.valueOf(10.99))
                .category("Old Category")
                .barcode("123456789")
                .stockQuantity(50)
                .build();

        Product updatedProduct = Product.builder()
                .id(productId)
                .name("New Name")
                .description("New Description")
                .price(BigDecimal.valueOf(15.99))
                .category("New Category")
                .barcode("987654321")
                .stockQuantity(30)
                .build();

        when(productService.getProductById(productId)).thenReturn(Optional.of(existingProduct));
        when(productService.saveProduct(any(Product.class))).thenReturn(updatedProduct);

        ResponseEntity<Product> response = productController.updateProduct(productId, updatedProduct);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedProduct.getName(), response.getBody().getName());
        assertEquals(updatedProduct.getPrice(), response.getBody().getPrice());
        assertEquals(updatedProduct.getPrice(), response.getBody().getPrice());

        verify(productService).getProductById(productId);
        verify(productService).saveProduct(updatedProduct);
    }

    @Test
    void shouldReturnNotFoundWhenProductDoesNotExist() {
        Long productId = 1L;
        Product updatedProduct = Product.builder()
                .id(productId)
                .name("New Name")
                .description("New Description")
                .price(BigDecimal.valueOf(15.99))
                .category("New Category")
                .barcode("987654321")
                .stockQuantity(30)
                .build();

        when(productService.getProductById(productId)).thenReturn(Optional.empty());

        ResponseEntity<Product> response = productController.updateProduct(productId, updatedProduct);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(productService).getProductById(productId);
        verify(productService, never()).saveProduct(any(Product.class));
    }

}
