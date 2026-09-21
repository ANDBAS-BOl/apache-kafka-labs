package com.appandres.productsmicroservice.application.handler.impl;

import com.appandres.productsmicroservice.application.dto.request.CreateProductRequest;
import com.appandres.productsmicroservice.application.handler.IProductHandler;
import com.appandres.productsmicroservice.application.mapper.IProductDtoMapper;
import com.appandres.productsmicroservice.domain.api.IProductServicePort;
import com.appandres.productsmicroservice.domain.model.ProductModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductHandlerImpl implements IProductHandler {
    private final IProductServicePort productServicePort;
    private final IProductDtoMapper productDtoMapper;

    @Override
    public String createProduct(CreateProductRequest createProductRequest) {
        ProductModel productModel = productDtoMapper.toProductModel(createProductRequest);
        return productServicePort.createProduct(productModel);
    }
}
