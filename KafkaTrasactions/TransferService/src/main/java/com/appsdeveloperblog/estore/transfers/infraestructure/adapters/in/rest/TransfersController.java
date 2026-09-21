package com.appsdeveloperblog.estore.transfers.infraestructure.adapters.in.rest;

import com.appsdeveloperblog.estore.transfers.application.dto.request.TransferRequest;
import com.appsdeveloperblog.estore.transfers.application.handler.ITransferHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador de entrada REST. Recibe la petición HTTP y delega en el handler
 * de aplicación, sin conocer detalles del dominio ni de la mensajería.
 */
@RestController
@RequestMapping("/transfers")
@RequiredArgsConstructor
public class TransfersController {

    private final ITransferHandler transferHandler;

    @PostMapping
    public boolean transfer(@RequestBody TransferRequest transferRequest) {
        return transferHandler.transfer(transferRequest);
    }
}
