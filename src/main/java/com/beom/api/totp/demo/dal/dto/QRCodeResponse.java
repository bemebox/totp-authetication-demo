package com.beom.api.totp.demo.dal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * QRCode response data transaction object.
 *
 * @author beom
 * @since 2024/03/16
 */
public record QRCodeResponse(
        @JsonProperty("qr_code")
        String qrCode,
        @JsonProperty("issuer")
        String issuer
) {}
