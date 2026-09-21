package com.appandres.productsmicroservice.application.dto.request;

import java.math.BigDecimal;

public record CreateProductRequest(
        String title,
        BigDecimal price,
        Integer quantity) {
}
