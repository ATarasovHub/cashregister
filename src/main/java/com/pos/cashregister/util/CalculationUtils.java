package com.pos.cashregister.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculationUtils {

    public static BigDecimal calculateVatForProduct(BigDecimal price, int quantity, double vatRate) {
        BigDecimal total = price.multiply(BigDecimal.valueOf(quantity));
        BigDecimal vat = total.multiply(BigDecimal.valueOf(vatRate));

        return vat.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateSubtotal(BigDecimal price, int quantity) {
        BigDecimal total = price.multiply(BigDecimal.valueOf(quantity));
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateTotal(BigDecimal subtotal, BigDecimal vat) {
        BigDecimal total = subtotal.add(vat);
        return total.setScale(2, RoundingMode.HALF_UP);
    }
}
