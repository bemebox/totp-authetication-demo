package com.beom.api.totp.demo.controller;

import com.beom.api.totp.demo.dal.dto.QRCodeResponse;
import com.beom.api.totp.demo.dal.dto.TotpRequest;
import com.beom.api.totp.demo.exception.GenerateQRCodeImageException;
import com.beom.api.totp.demo.service.ITotpService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * class that represents the authentication controller
 *
 * @author beom
 * @since 2024/03/16
 */
@Slf4j
@RestController
@RequestMapping(path = "/v1.0/totp")
public class TotpController {

    private final ITotpService totpService;

    public TotpController(ITotpService totpService) {
        this.totpService = totpService;
    }

    @PostMapping(path = "/{username}/qrcode", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QRCodeResponse> getQRCodeImage(@PathVariable("username") String username, @Valid @RequestBody TotpRequest totpRequest) {
        log.debug("POST /{}/qrcode", username);

        String qrCode64Image = this.totpService.generateQRCodeBase64Image(totpRequest.issuer(), username);
        if (!StringUtils.hasText(qrCode64Image)) {
            throw new GenerateQRCodeImageException("No QRCode generated for the username " + username);
        }

        QRCodeResponse qrCodeResponse = new QRCodeResponse(qrCode64Image, totpRequest.issuer());

        return ResponseEntity.ok().body(qrCodeResponse);
    }
}
