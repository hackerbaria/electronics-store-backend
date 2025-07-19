package com.altech.store.service;

import com.altech.store.dto.Receipt;
import com.altech.store.exception.InsufficientStockException;

public interface BasketService {
    void addItem(Long customerId, Long productId, int quantity) throws InsufficientStockException;
    void removeItem(Long customerId, Long productId, int quantity);
    Receipt getReceipt(Long customerId);
}
