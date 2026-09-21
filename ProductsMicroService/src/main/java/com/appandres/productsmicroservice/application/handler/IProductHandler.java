package com.appandres.productsmicroservice.application.handler;

import com.appandres.productsmicroservice.application.dto.request.CreateProductRequest;

public interface IProductHandler {
    String createProduct(CreateProductRequest createProductRequest);
}
