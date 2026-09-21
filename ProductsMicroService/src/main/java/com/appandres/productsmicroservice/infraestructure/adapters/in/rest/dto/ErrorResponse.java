package com.appandres.productsmicroservice.infraestructure.adapters.in.rest.dto;

import java.time.Instant;

public record ErrorResponse(String code, String message, Instant timestamp) {
}
