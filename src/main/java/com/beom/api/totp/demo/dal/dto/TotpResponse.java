package com.beom.api.totp.demo.dal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TOTP response data transaction object.
 *
 * @author beom
 * @since 2024/03/16
 */
public record TotpResponse(
        @JsonProperty("issuer")
        String issuer,
        @JsonProperty("valid")
        boolean valid
) {}
