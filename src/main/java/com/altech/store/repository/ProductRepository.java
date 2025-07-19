package com.altech.store.repository;

import com.altech.store.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;


@Repository
public interface ProductRepository  extends JpaRepository<Product, Long> {
    @Query("""
           SELECT p FROM Product p
           WHERE (:category IS NULL OR p.category = :category)
             AND (:minPrice IS NULL OR p.price >= :minPrice)
             AND (:maxPrice IS NULL OR p.price <= :maxPrice)
             AND (:availableOnly = 0 OR p.stock > 0)
           """)
    Page<Product> findFilteredProducts(
            @Param("category") String category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("availableOnly") int availableOnly,
            Pageable pageable
    );
}
