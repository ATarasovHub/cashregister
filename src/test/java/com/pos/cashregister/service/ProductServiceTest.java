package com.pos.cashregister.service;

import com.pos.cashregister.model.Product;
import com.pos.cashregister.repository.JpaProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductService service;
    @Mock
    private JpaProductRepository repository;

    @Test
    void shouldReturnListOfProductsWhenProductsExist() {
        List<Product> mockProducts = List.of(
                Product.builder()
                        .id(1L)
                        .name("Product 1")
                        .build(),

                Product.builder()
                        .id(2L)
                        .name("Product 2")
                        .build()
        );

        when(repository.findAll()).thenReturn(mockProducts);

        List<Product> result = service.getAllProducts();

        assertEquals(2, result.size());
        assertEquals("Product 1", result.getFirst().getName());
        assertEquals("Product 2", result.getLast().getName());

        verify(repository).findAll();
    }

    @Test
    void shouldReturnProductByIdWhenProductExist() {
        Long productId = 1L;
        Product mockProduct = new Product();
        mockProduct.setId(productId);

        when(repository.findById(productId)).thenReturn(Optional.of(mockProduct));

        Optional<Product> result = service.getProductById(productId);

        assertTrue(result.isPresent());
        assertEquals(mockProduct, result.get());
        verify(repository).findById(productId);
    }

    @Test
    void shouldSaveProductSuccessfully() {
        Product product = Product.builder()
                .id(1L)
                .build();

        when(repository.save(product)).thenReturn(product);

        Product savedProduct = service.saveProduct(product);

        assertNotNull(savedProduct);
        assertEquals(savedProduct.getId(), product.getId());
        verify(repository).save(product);
    }

    @Test
    void shouldDeleteProductSuccessfully() {
        Long productId = 1L;

        service.deleteProduct(productId);

        verify(repository).deleteById(productId);
    }

    @Test
    void shouldReturnTrueWhenProductAvailable() {
        Long productId = 1L;
        int requestedQuantity = 5;

        Product product = new Product();
        product.setId(productId);
        product.setStockQuantity(10);

        when(repository.findById(productId)).thenReturn(Optional.of(product));

        boolean result = service.isProductAvailable(productId, requestedQuantity);

        assertTrue(result);
        verify(repository).findById(productId);

    }

    @Test
    void shouldReturnFalseWhenProductNotAvailable() {
       Long productId = 1L;
       int requestedQuantity = 10;

       Product product = new Product();
       product.setId(productId);
       product.setStockQuantity(10);

       when(repository.findById(productId)).thenReturn(Optional.empty());

       boolean result = service.isProductAvailable(productId,requestedQuantity);

       assertFalse(result);
       verify(repository).findById(productId);
    }

    @Test
    void shouldUpdateStockSuccessfullyWhenQuantityChangeIsValid() {
        Long productId = 1L;
        int quantityChange = 5;

        Product product = new Product();
        product.setId(productId);
        product.setStockQuantity(10);

        when(repository.findById(productId)).thenReturn(Optional.of(product));

        boolean result = service.updateStock(productId,quantityChange);

        assertTrue(result);
        assertEquals(5,product.getStockQuantity());
        verify(repository).save(product);
    }

    @Test
    void shouldNotUpdateStockSuccessfullyWhenQuantityChangeIsInvalid() {
        Long productId = 1L;
        int quantityChange = 10;

        Product product = new Product();
        product.setId(productId);
        product.setStockQuantity(5);

        when(repository.findById(productId)).thenReturn(Optional.of(product));

        boolean result = service.updateStock(productId,quantityChange);

        assertFalse(result);
        assertEquals(5, product.getStockQuantity());
        verify(repository, never()).save(product);
    }

}
