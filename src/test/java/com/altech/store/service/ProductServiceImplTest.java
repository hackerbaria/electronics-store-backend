package com.altech.store.service;

import com.altech.store.entity.Product;
import com.altech.store.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProduct() {
        Product inputProduct = new Product();
        inputProduct.setId(99L); // Should be nullified
        Product savedProduct = new Product();
        savedProduct.setId(1L);

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        Product result = productService.create(inputProduct);

        assertThat(result.getId()).isEqualTo(1L);
        verify(productRepository).save(argThat(p -> p.getId() == null));
    }

    @Test
    void testDeleteProduct() {
        Long productId = 42L;

        productService.delete(productId);

        verify(productRepository).deleteById(productId);
    }

    @Test
    void testGetAllProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Product product = new Product();
        Page<Product> page = new PageImpl<>(List.of(product));

        when(productRepository.findAll(pageable)).thenReturn(page);

        Page<Product> result = productService.getAll(pageable);

        List<Product> products = result.getContent();

        assertThat(products.size()).isEqualTo(1);
        verify(productRepository).findAll(pageable);
    }

    @Test
    void testFindFilteredProducts() {
        String category = "Electronics";
        BigDecimal minPrice = BigDecimal.valueOf(10);
        BigDecimal maxPrice = BigDecimal.valueOf(1000);
        boolean availableOnly = true;
        Pageable pageable = PageRequest.of(0, 5);
        Page<Product> page = new PageImpl<>(List.of(new Product()));

        when(productRepository.findFilteredProducts(category, minPrice, maxPrice, 1, pageable)).thenReturn(page);

        Page<Product> result = productService.findProducts(category, minPrice, maxPrice, availableOnly, pageable);

        // Then
        List<Product> products = result.getContent();
        assertThat(products.size()).isEqualTo(1);
        verify(productRepository).findFilteredProducts(category, minPrice, maxPrice, 1, pageable);
    }
}
