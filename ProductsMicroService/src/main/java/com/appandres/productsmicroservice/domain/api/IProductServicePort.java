package com.appandres.productsmicroservice.domain.api;

import com.appandres.productsmicroservice.domain.model.ProductModel;

public interface IProductServicePort {

    String createProduct(ProductModel productModel);
}
