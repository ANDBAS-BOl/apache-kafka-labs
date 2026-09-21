package com.appandres.productsmicroservice.domain.spi;

import com.appandres.productsmicroservice.domain.model.ProductModel;

public interface IProductPersistencePort {
    ProductModel saveProduct(ProductModel productModel);
}
