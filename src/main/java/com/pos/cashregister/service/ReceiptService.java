package com.pos.cashregister.service;

import com.pos.cashregister.dto.ReceiptDTO;
import com.pos.cashregister.dto.ReceiptItemDTO;
import com.pos.cashregister.model.Product;
import com.pos.cashregister.model.Receipt;
import com.pos.cashregister.model.ReceiptItem;
import com.pos.cashregister.repository.JpaReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final JpaReceiptRepository receiptRepository;
    private final ProductService productService;

    public List<Receipt> getAllReceipts() {
        return receiptRepository.findAll();
    }

    public Optional<Receipt> getReceiptById(Long id) {
        return receiptRepository.findById(id);
    }

    @Transactional
    public Receipt createReceipt(ReceiptDTO receiptDTO) {
        Receipt receipt = new Receipt();
        receipt.setCashierName(receiptDTO.getCashierName());
        receipt.setPaymentMethod(receiptDTO.getPaymentMethod());
        receipt.setDateTime(LocalDateTime.now());

        List<ReceiptItem> items = new ArrayList<>();
        boolean allProductsAvailable = true;

        // Check if all products are available in the required quantity
        for (ReceiptItemDTO itemDTO : receiptDTO.getItems()) {
            if (!productService.isProductAvailable(itemDTO.getProductId(), itemDTO.getQuantity())) {
                allProductsAvailable = false;
                break;
            }
        }

        if (!allProductsAvailable) {
            throw new RuntimeException("One or more products are not available in the required quantity");
        }

        // Build receipt items and update product stock
        for (ReceiptItemDTO itemDTO : receiptDTO.getItems()) {
            Optional<Product> productOpt = productService.getProductById(itemDTO.getProductId());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                ReceiptItem item = ReceiptItem.builder()
                        .productId(product.getId())
                        .productName(product.getName())
                        .price(product.getPrice())
                        .quantity(itemDTO.getQuantity())
                        .total(product.getPrice()
                                .multiply(BigDecimal.valueOf(itemDTO.getQuantity()))
                                .setScale(2, BigDecimal.ROUND_HALF_UP))
                        .build();
                items.add(item);
                productService.updateStock(product.getId(), itemDTO.getQuantity());
            }
        }

        receipt.setItems(items);

        // Calculate subtotal, tax (10%) and total
        BigDecimal subtotal = BigDecimal.ZERO;
        for (ReceiptItem item : items) {
            subtotal = subtotal.add(item.getTotal());
        }
        BigDecimal taxAmount = subtotal.multiply(new BigDecimal("0.10")).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal total = subtotal.add(taxAmount).setScale(2, BigDecimal.ROUND_HALF_UP);

        receipt.setTaxAmount(taxAmount);
        receipt.setTotal(total);

        return receiptRepository.save(receipt);
    }

    public void deleteReceipt(Long id) {
        receiptRepository.deleteById(id);
    }

    public BigDecimal calculateDailyTotal() {
        List<Receipt> allReceipts = receiptRepository.findAll();
        return allReceipts.stream()
                .map(Receipt::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Receipt> searchReceiptsByDateRange(String startDate, String endDate) {
        return receiptRepository.findAll();
    }
}
