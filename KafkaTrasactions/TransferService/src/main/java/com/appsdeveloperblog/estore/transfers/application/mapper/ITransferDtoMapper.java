package com.appsdeveloperblog.estore.transfers.application.mapper;

import com.appsdeveloperblog.estore.transfers.application.dto.request.TransferRequest;
import com.appsdeveloperblog.estore.transfers.domain.model.TransferModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct: convierte el DTO de entrada en el modelo de dominio.
 * El {@code transferId} se genera en el caso de uso, por eso se ignora aquí.
 */
@Mapper(componentModel = "spring")
public interface ITransferDtoMapper {

    @Mapping(target = "transferId", ignore = true)
    TransferModel toTransferModel(TransferRequest transferRequest);
}
