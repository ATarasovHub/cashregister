package com.pos.cashregister.service;

import com.pos.cashregister.model.Product;
import com.pos.cashregister.repository.JpaProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProductService {

    private final JpaProductRepository productRepository;

    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        log.info("Retrieved products: " + products);
        return products;
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public boolean isProductAvailable(Long id, int quantity) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            return product.getStockQuantity() >= quantity;
        }
        return false;
    }

    public boolean updateStock(Long id, int quantityChange) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            int newQuantity = product.getStockQuantity() - quantityChange;

            if (newQuantity >= 0) {
                product.setStockQuantity(newQuantity);
                productRepository.save(product);
                return true;
            }
        }
        return false;
    }
}

