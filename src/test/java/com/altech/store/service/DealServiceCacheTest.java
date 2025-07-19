package com.altech.store.service;


import com.altech.store.dto.DealType;
import com.altech.store.entity.Deal;
import com.altech.store.repository.DealRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@EnableCaching
public class DealServiceCacheTest {

    @Autowired
    private DealService dealService;

    @MockitoBean
    private DealRepository dealRepository;

    @BeforeEach
    void setUp() {
        LocalDateTime future = LocalDateTime.now().plusDays(1);
        List<Deal> mockDeals = List.of(
                Deal.builder()
                        .id(1L)
                        .productIds(List.of(1001L, 1002L))
                        .dealType(DealType.FIXED_AMOUNT_DISCOUNT) // or whatever enum value is valid
                        .discountValue(10.0)
                        .expiration(future)
                        .build(),
                Deal.builder()
                        .id(2L)
                        .productIds(List.of(1003L, 1004L))
                        .dealType(DealType.BUY_1_GET_2ND_50_PERCENT) // another valid enum
                        .discountValue(20.0)
                        .expiration(future)
                        .build()
        );

        Mockito.when(dealRepository.findAll()).thenReturn(mockDeals);
    }

    @Test
    void testGetAllActiveDealsCaching() {
        // First call - should trigger repository
        List<Deal> first = dealService.getAllActiveDeals();

        // Second call - should be served from cache
        List<Deal> second = dealService.getAllActiveDeals();

        // Third call - should be served from cache
        List<Deal> third = dealService.getAllActiveDeals();

        // Assert the results are equal
        Assertions.assertEquals(first, second);

        // Verify that findAll was only called once (due to caching)
        Mockito.verify(dealRepository, Mockito.times(1)).findAll();
    }
}
