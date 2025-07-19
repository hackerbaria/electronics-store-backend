package com.altech.store.controller;

import com.altech.store.entity.Product;
import com.altech.store.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/admin/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "APIs for managing products")
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "Create a new product", description = "Adds a new product to the database")
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        return ResponseEntity.ok(productService.create(product));
    }

    @Operation(summary = "Delete a product", description = "Deletes a product by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all products", description = "Returns a paginated list of products")
    @GetMapping
    public ResponseEntity<Page<Product>> getAll(@Parameter(description = "Page number (default is 0)")
                                                    @RequestParam(defaultValue = "0") int page,
                                                @Parameter(description = "Page size (default is 10)")
                                                    @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.getAll(PageRequest.of(page, size)));
    }
}
