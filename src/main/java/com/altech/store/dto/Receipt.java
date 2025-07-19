package com.altech.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class Receipt {

    private final List<Line> items;
    private final BigDecimal total;

    @Getter
    @AllArgsConstructor
    public static class Line {
        private final String productName;
        private final int quantity;
        private final BigDecimal price;
        private final BigDecimal subtotal;
        private final BigDecimal discount;
    }
}
