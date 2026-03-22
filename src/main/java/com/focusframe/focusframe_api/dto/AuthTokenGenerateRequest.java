package com.focusframe.focusframe_api.dto;

public record AuthTokenGenerateRequest(
        String ipAddress,
        String callbackUrl
) {
}
