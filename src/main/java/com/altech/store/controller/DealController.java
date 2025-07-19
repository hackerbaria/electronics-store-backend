package com.altech.store.controller;


import com.altech.store.dto.DealRequest;
import com.altech.store.entity.Deal;
import com.altech.store.service.DealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/admin/deals")
@RequiredArgsConstructor
@Tag(name = "Deal Management", description = "APIs for managing deals")
public class DealController {

    private final DealService dealService;

    @Operation(summary = "Create a new deal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deal created successfully",
                    content = @Content(schema = @Schema(implementation = Deal.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<Deal> createDeal(@RequestBody DealRequest request) {
        return ResponseEntity.ok(dealService.createDeal(request));
    }

    @Operation(summary = "Get all active deals")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of active deals",
                    content = @Content(schema = @Schema(implementation = Deal.class)))
    })
    @GetMapping("/active")
    public ResponseEntity<List<Deal>> getAllActiveDeals() {
        List<Deal> activeDeals = dealService.getAllActiveDeals();
        return ResponseEntity.ok(activeDeals);
    }
}
