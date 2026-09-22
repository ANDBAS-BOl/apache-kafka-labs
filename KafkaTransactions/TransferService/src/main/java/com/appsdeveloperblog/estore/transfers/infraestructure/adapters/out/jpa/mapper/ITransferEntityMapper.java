package com.appsdeveloperblog.estore.transfers.infraestructure.adapters.out.jpa.mapper;

import com.appsdeveloperblog.estore.transfers.domain.model.TransferModel;
import com.appsdeveloperblog.estore.transfers.infraestructure.adapters.out.jpa.entity.TransferEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct: convierte entre la entidad JPA y el modelo de dominio, evitando
 * que el dominio dependa de detalles de persistencia.
 * <p>
 * El modelo usa {@code recepientId} y la entidad {@code recipientId}, por eso el
 * mapeo entre ambos se declara de forma explícita.
 */
@Mapper(componentModel = "spring")
public interface ITransferEntityMapper {

    @Mapping(source = "recepientId", target = "recipientId")
    TransferEntity toEntity(TransferModel transferModel);

    @Mapping(source = "recipientId", target = "recepientId")
    TransferModel toModel(TransferEntity transferEntity);
}
