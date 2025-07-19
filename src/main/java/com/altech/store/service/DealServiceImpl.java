package com.altech.store.service;

import com.altech.store.dto.DealRequest;
import com.altech.store.entity.Deal;
import com.altech.store.exception.ResourceNotFoundException;
import com.altech.store.repository.DealRepository;
import com.altech.store.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {

    private final DealRepository dealRepository;
    private final ProductRepository productRepository;

    @Override
    public Deal createDeal(DealRequest request) {
        // Optional: Validate that all product IDs exist
        List<Long> productIds = request.getProductIds();
        for (Long productId : productIds) {
            if (!productRepository.existsById(productId)) {
                throw new ResourceNotFoundException("Product not found: " + productId);
            }
        }

        Deal deal = new Deal();
        deal.setDealType(request.getDealType());
        deal.setDiscountValue(request.getValue());
        deal.setExpiration(request.getExpiration());
        deal.setProductIds(productIds);

        return dealRepository.save(deal);
    }

    @Cacheable(value = "activeDeals")
    public List<Deal> getAllActiveDeals() {
        LocalDateTime now = LocalDateTime.now();
        return dealRepository.findAll().stream()
                .filter(deal -> deal.getExpiration().isAfter(now))
                .collect(Collectors.toList());
    }
}
