package com.beom.api.totp.demo.dal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

/**
 * TOTP request data transaction object.
 *
 * @author beom
 * @since 2024/03/16
 */
public record TotpRequest(
        @NotNull(message = "issuer must not be null")
        @JsonProperty("issuer")
        String issuer
) {}
