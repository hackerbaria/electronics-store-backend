package com.altech.store.entity;

import com.altech.store.dto.DealType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "deal_products", joinColumns = @JoinColumn(name = "deal_id"))
    @Column(name = "product_id")
    private List<Long> productIds = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private DealType dealType;

    private double discountValue;

    private LocalDateTime expiration;
}
