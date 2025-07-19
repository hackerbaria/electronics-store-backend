package com.altech.store.dto;

import lombok.Data;

@Data
public class BasketItemRequest {
    private Long productId;
    private int quantity;
}
