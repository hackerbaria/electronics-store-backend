package com.altech.store.controller;

import com.altech.store.dto.BasketItemRequest;
import com.altech.store.dto.Receipt;
import com.altech.store.exception.InsufficientStockException;
import com.altech.store.service.BasketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/customers/{customerId}/basket")
@RequiredArgsConstructor
@Tag(name = "Basket", description = "Operations related to shopping basket")
public class BasketController {

    private final BasketService basketService;

    @Operation(summary = "Add item to basket", description = "Adds a product to the customer's basket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item added successfully"),
            @ApiResponse(responseCode = "400", description = "Insufficient stock", content = @Content)
    })
    @PostMapping("/items")
    public ResponseEntity<?> addItemToBasket(@Parameter(description = "Customer ID") @PathVariable Long customerId,
                                             @RequestBody BasketItemRequest request) {
        try {
            basketService.addItem(customerId, request.getProductId(), request.getQuantity());
            return ResponseEntity.ok().build();
        } catch (InsufficientStockException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Remove item from basket", description = "Removes a product from the customer's basket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item removed successfully")
    })
    @DeleteMapping("/items")
    public ResponseEntity<?> removeItemFromBasket(@Parameter(description = "Customer ID") @PathVariable Long customerId,
                                                  @RequestBody BasketItemRequest request) {
        basketService.removeItem(customerId, request.getProductId(), request.getQuantity());
        return ResponseEntity.ok().build();
    }

    // Get receipt for a customer
    @Operation(summary = "Get receipt", description = "Generates a receipt for the customer's basket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receipt retrieved",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Receipt.class)))
    })
    @GetMapping("/receipt")
    public ResponseEntity<Receipt> getReceipt(@Parameter(description = "Customer ID") @PathVariable Long customerId) {
        Receipt receipt = basketService.getReceipt(customerId);
        return ResponseEntity.ok(receipt);
    }
}
