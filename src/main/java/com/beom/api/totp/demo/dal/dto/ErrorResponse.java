package com.beom.api.totp.demo.dal.dto;

import java.time.Instant;
import java.util.List;

/**
 * Error response data transaction object.
 *
 * @author beom
 * @since: 2024/03/16
 */
public record ErrorResponse(
        int status,
        List<String> errors,
        Instant timestamp
) {}
