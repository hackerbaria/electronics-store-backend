package com.altech.store.controller;


import com.altech.store.config.SecurityConfig;
import com.altech.store.dto.BasketItemRequest;
import com.altech.store.dto.Receipt;
import com.altech.store.exception.InsufficientStockException;
import com.altech.store.service.BasketService;
import com.altech.store.service.DealService;
import com.altech.store.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BasketController.class)
@Import(SecurityConfig.class)
public class BasketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BasketService basketService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long customerId = 1L;

    private BasketItemRequest basketItemRequest;


    @TestConfiguration
    static class MockConfig {
        @Bean
        public BasketService basketService() {
            return mock(BasketService.class);
        }
    }

    @BeforeEach
    void setUp() {
        basketItemRequest = new BasketItemRequest();
        basketItemRequest.setProductId(10L);
        basketItemRequest.setQuantity(2);
    }

    @Test
    void addItemToBasket_shouldReturnOk() throws Exception {
        // No exception thrown = success
        doNothing().when(basketService).addItem(eq(customerId), eq(10L), eq(2));

        mockMvc.perform(post("/v1/api/customers/{customerId}/basket/items", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(basketItemRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void addItemToBasket_shouldReturnBadRequest_whenStockInsufficient() throws Exception {
        doThrow(new InsufficientStockException("Not enough stock"))
                .when(basketService).addItem(eq(customerId), eq(10L), eq(2));

        mockMvc.perform(post("/v1/api/customers/{customerId}/basket/items", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(basketItemRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Not enough stock"));
    }

    @Test
    void removeItemFromBasket_shouldReturnOk() throws Exception {
        doNothing().when(basketService).removeItem(eq(customerId), eq(10L), eq(2));

        mockMvc.perform(delete("/v1/api/customers/{customerId}/basket/items", customerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(basketItemRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void getReceipt_shouldReturnReceipt() throws Exception {
        List<Receipt.Line> lines = List.of(
                new Receipt.Line("Product A", 2, BigDecimal.valueOf(50), BigDecimal.valueOf(100), BigDecimal.ZERO),
                new Receipt.Line("Product B", 1, BigDecimal.valueOf(120), BigDecimal.valueOf(120), BigDecimal.valueOf(20))
        );
        Receipt receipt = new Receipt(lines, BigDecimal.valueOf(200));

        when(basketService.getReceipt(customerId)).thenReturn(receipt);

        mockMvc.perform(get("/v1/api/customers/{customerId}/basket/receipt", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(200))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].productName").value("Product A"))
                .andExpect(jsonPath("$.items[1].productName").value("Product B"));
    }
}
