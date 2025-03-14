package com.pos.cashregister.service;

import com.pos.cashregister.model.Product;
import com.pos.cashregister.repository.JpaProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final JpaProductRepository productRepository;

    @Autowired
    public ProductService(JpaProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
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

