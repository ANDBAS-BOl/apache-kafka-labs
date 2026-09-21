package com.appandres.productsmicroservice.domain.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductModel(
        String id,
        String title,
        BigDecimal price,
        Integer quantity
) {
}
