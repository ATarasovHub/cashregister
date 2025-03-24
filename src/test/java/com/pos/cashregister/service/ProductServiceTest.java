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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks private ProductService service;
    @Mock private JpaProductRepository repository;

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



}
