package com.altech.store.controller;

import com.altech.store.entity.Product;
import com.altech.store.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/v1/api/customer/products")
@RequiredArgsConstructor
@Tag(name = "Customer - Products", description = "Endpoints for customers to view products with filters")
public class ProductCustomerController {
    private final ProductService productService;

    // Filter products by category, price range, availability (stock > 0)
    @Operation(
            summary = "Get filtered products",
            description = "Filter products by category, price range, and availability",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered products"),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    @GetMapping
    public ResponseEntity<Page<Product>> getFilteredProducts(
            @Parameter(description = "Product category") @RequestParam(required = false) String category,
            @Parameter(description = "Minimum price") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Only show available products") @RequestParam(required = false, defaultValue = "true") boolean availableOnly,
            @Parameter(hidden = true) Pageable pageable
    ) {
        Page<Product> products = productService.findProducts(category, minPrice, maxPrice, availableOnly, pageable);
        return ResponseEntity.ok (products);
    }
}
