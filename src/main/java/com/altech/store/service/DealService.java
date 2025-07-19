package com.altech.store.service;

import com.altech.store.dto.DealRequest;
import com.altech.store.entity.Deal;

import java.util.List;

public interface DealService {
    Deal createDeal(DealRequest request);

    List<Deal> getAllActiveDeals();
}
