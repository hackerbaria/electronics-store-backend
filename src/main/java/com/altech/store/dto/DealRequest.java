package com.altech.store.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DealRequest {
    private List<Long> productIds;
    private DealType dealType;
    private double value;
    private LocalDateTime expiration;
}
