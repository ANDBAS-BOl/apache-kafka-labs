package com.appandres.productsmicroservice.infraestructure.adapters.persistence;

import com.appandres.productsmicroservice.domain.model.ProductModel;
import com.appandres.productsmicroservice.domain.spi.IProductPersistencePort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DummyProductPersistenceAdapter implements IProductPersistencePort {

    @Override
    public ProductModel saveProduct(ProductModel productModel) {
        String id = UUID.randomUUID().toString();
        return new ProductModel(id, productModel.title(), productModel.price(), productModel.quantity());
    }
}
