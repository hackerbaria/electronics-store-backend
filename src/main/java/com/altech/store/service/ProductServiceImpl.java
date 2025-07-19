package com.altech.store.service;

import com.altech.store.entity.Product;
import com.altech.store.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Product create(Product product) {
        product.setId(null);
        return productRepository.save(product);
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Page<Product> getAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> findProducts(String category, BigDecimal minPrice, BigDecimal maxPrice, boolean availableOnly, Pageable pageable) {
        return productRepository.findFilteredProducts(category, minPrice, maxPrice, availableOnly ? 1 : 0, pageable);
    }
}
