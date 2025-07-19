package com.altech.store.controller;


import com.altech.store.config.SecurityConfig;
import com.altech.store.entity.Product;
import com.altech.store.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @TestConfiguration
    static class MockConfig {
        @Bean
        public ProductService productService() {
            return mock(ProductService.class);
        }
    }

    @Test
    void testCreateProduct_Unauthenticated_ShouldReturn401() throws Exception {
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .category("Electronics")
                .price(BigDecimal.valueOf(100))
                .stock(10)
                .version(0L)
                .build();

        mockMvc.perform(post("/v1/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isUnauthorized()); // 401 because not logged in
    }

    @Test
    @WithMockUser(username = "user") // Wrong role
    void testCreateProduct_WithInsufficientRole_ShouldReturn403() throws Exception {
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .category("Electronics")
                .price(BigDecimal.valueOf(100))
                .stock(10)
                .version(0L)
                .build();

        mockMvc.perform(post("/v1/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isForbidden()); // 403 because role is insufficient
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateProduct() throws Exception {
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .category("Electronics")
                .price(BigDecimal.valueOf(100))
                .stock(10)
                .version(0L)
                .build();

        when(productService.create(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/v1/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteProduct() throws Exception {
        Long id = 1L;

        doNothing().when(productService).delete(id);

        mockMvc.perform(delete("/v1/api/admin/products/{id}", id))
                .andExpect(status().isNoContent());

        verify(productService).delete(id);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllProducts() throws Exception {
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .category("Electronics")
                .price(BigDecimal.valueOf(100))
                .stock(10)
                .version(0L)
                .build();

        Page<Product> page = new PageImpl<>(List.of(product));
        when(productService.getAll(PageRequest.of(0, 10))).thenReturn(page);

        mockMvc.perform(get("/v1/api/admin/products")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Test Product"));
    }
}
