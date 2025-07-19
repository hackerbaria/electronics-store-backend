package com.altech.store.service;

import com.altech.store.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ProductService {

    Product create(Product product);
    void delete(Long id);
    Page<Product> getAll(Pageable pageable);

    Page<Product> findProducts(String category, BigDecimal minPrice, BigDecimal maxPrice, boolean availableOnly,
                               Pageable pageable);
}
