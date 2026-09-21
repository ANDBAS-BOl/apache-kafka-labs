package com.appandres.productsmicroservice.application.mapper;

import com.appandres.productsmicroservice.application.dto.request.CreateProductRequest;
import com.appandres.productsmicroservice.domain.model.ProductModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IProductDtoMapper {

    @Mapping(target = "id", ignore = true)
    ProductModel toProductModel(CreateProductRequest createProductRequest);
}
