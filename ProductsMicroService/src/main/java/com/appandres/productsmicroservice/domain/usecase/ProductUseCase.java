package com.appandres.productsmicroservice.domain.usecase;

import com.appandres.productsmicroservice.domain.api.IProductServicePort;
import com.appandres.core.domain.event.ProductCreatedEvent;
import com.appandres.productsmicroservice.domain.model.ProductModel;
import com.appandres.productsmicroservice.domain.spi.IProductMessagingPort;
import com.appandres.productsmicroservice.domain.spi.IProductPersistencePort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProductUseCase implements IProductServicePort {
    private final IProductPersistencePort persistencePort;
    private final IProductMessagingPort messagingPort;

    @Override
    public String createProduct(ProductModel productModel) {
        ProductModel persisted = persistencePort.saveProduct(productModel);

        ProductCreatedEvent event = new ProductCreatedEvent(
                persisted.id(),
                persisted.title(),
                persisted.price(),
                persisted.quantity()
        );

        messagingPort.sendProductCreatedEvent(event);
        return persisted.id();
    }
}
