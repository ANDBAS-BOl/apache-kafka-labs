package com.appandres.productsmicroservice.infraestructure.adapters.in.rest;

import com.appandres.productsmicroservice.application.dto.request.CreateProductRequest;
import com.appandres.productsmicroservice.application.handler.IProductHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductHandler productHandler;

    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody CreateProductRequest productDto) {
        String productId = productHandler.createProduct(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productId);
    }
}
