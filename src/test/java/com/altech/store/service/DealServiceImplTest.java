package com.altech.store.service;

import com.altech.store.dto.DealRequest;
import com.altech.store.dto.DealType;
import com.altech.store.entity.Deal;
import com.altech.store.exception.ResourceNotFoundException;
import com.altech.store.repository.DealRepository;
import com.altech.store.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DealServiceImplTest {

    @Mock
    private DealRepository dealRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private DealServiceImpl dealService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createDeal_shouldCreateDeal_whenAllProductIdsExist() {
        // Arrange
        List<Long> productIds = List.of(1L, 2L);
        DealRequest request = new DealRequest();
        request.setProductIds(productIds);
        request.setDealType(DealType.PERCENTAGE_DISCOUNT);
        request.setValue(20.0);
        request.setExpiration(LocalDateTime.now().plusDays(3));

        for (Long productId : productIds) {
            when(productRepository.existsById(productId)).thenReturn(true);
        }

        Deal expectedDeal = new Deal();
        expectedDeal.setId(1L);
        expectedDeal.setDealType(DealType.PERCENTAGE_DISCOUNT);
        expectedDeal.setDiscountValue(20.0);
        expectedDeal.setExpiration(request.getExpiration());
        expectedDeal.setProductIds(productIds);

        when(dealRepository.save(any(Deal.class))).thenReturn(expectedDeal);

        // Act
        Deal result = dealService.createDeal(request);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDeal.getDealType(), result.getDealType());
        assertEquals(expectedDeal.getDiscountValue(), result.getDiscountValue());
        assertEquals(expectedDeal.getExpiration(), result.getExpiration());
        assertEquals(expectedDeal.getProductIds(), result.getProductIds());

        for (Long productId : productIds) {
            verify(productRepository, times(1)).existsById(productId);
        }

        verify(dealRepository, times(1)).save(any(Deal.class));
    }

    @Test
    void createDeal_shouldThrowException_whenProductIdNotFound() {
        // Arrange
        List<Long> productIds = List.of(1L, 99L);
        DealRequest request = new DealRequest();
        request.setProductIds(productIds);
        request.setDealType(DealType.FIXED_AMOUNT_DISCOUNT);
        request.setValue(10.0);
        request.setExpiration(LocalDateTime.now().plusDays(5));

        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> dealService.createDeal(request));

        //assertEquals("Product not found: 99", exception.getMessage());
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).existsById(99L);
        verify(dealRepository, never()).save(any());
    }

    @Test
    void getAllActiveDeals_shouldReturnOnlyActiveDeals() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        Deal activeDeal = new Deal();
        activeDeal.setId(1L);
        activeDeal.setExpiration(now.plusDays(1));

        Deal expiredDeal = new Deal();
        expiredDeal.setId(2L);
        expiredDeal.setExpiration(now.minusDays(1));

        when(dealRepository.findAll()).thenReturn(List.of(activeDeal, expiredDeal));

        // Act
        List<Deal> result = dealService.getAllActiveDeals();

        // Assert
        assertEquals(1, result.size());
        assertEquals(activeDeal.getId(), result.get(0).getId());
        verify(dealRepository, times(1)).findAll();
    }
}
