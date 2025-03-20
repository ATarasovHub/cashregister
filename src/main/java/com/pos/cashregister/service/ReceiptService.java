package com.pos.cashregister.service;

import com.pos.cashregister.model.Product;
import com.pos.cashregister.model.Receipt;
import com.pos.cashregister.model.ReceiptItem;
import com.pos.cashregister.repository.JpaReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    public Receipt createReceipt(Receipt incomingReceipt) {
        Receipt receipt = new Receipt();
        receipt.setCashierName(incomingReceipt.getCashierName());
        receipt.setPaymentMethod(incomingReceipt.getPaymentMethod());
        receipt.setDateTime(LocalDateTime.now());

        List<ReceiptItem> items = new ArrayList<>();

        BigDecimal grandSubtotal = BigDecimal.ZERO;
        BigDecimal grandVat = BigDecimal.ZERO;

        for (ReceiptItem item : incomingReceipt.getItems()) {
            Optional<Product> productOpt = productService.getProductById(item.getProductId());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();

                BigDecimal itemSubtotal = product.getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()))
                        .setScale(2, RoundingMode.HALF_UP);

                double vatRate = 0.19;
                if (product.getProductType() != null
                        && product.getProductType().name().equals("ESSENTIAL")) {
                    vatRate = 0.07;
                }

                BigDecimal itemVat = itemSubtotal
                        .multiply(BigDecimal.valueOf(vatRate))
                        .setScale(2, RoundingMode.HALF_UP);

                BigDecimal itemTotal = itemSubtotal.add(itemVat).setScale(2, RoundingMode.HALF_UP);

                ReceiptItem newItem = ReceiptItem.builder()
                        .productId(product.getId())
                        .productName(product.getName())
                        .price(product.getPrice())
                        .quantity(item.getQuantity())
                        .total(itemTotal)
                        .build();

                items.add(newItem);

                productService.updateStock(product.getId(), item.getQuantity());

                grandSubtotal = grandSubtotal.add(itemSubtotal);
                grandVat = grandVat.add(itemVat);
            }
        };

        receipt.setItems(items);

        BigDecimal grandTotal = grandSubtotal.add(grandVat).setScale(2, RoundingMode.HALF_UP);

        receipt.setTaxAmount(grandVat);
        receipt.setTotal(grandTotal);

        receipt.setPaymentReceived(incomingReceipt.getPaymentReceived());
        receipt.setChangeAmount(incomingReceipt.getChangeAmount());

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
