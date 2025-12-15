package com.spacecodee.securityspacee.shared.adapter.in.web.dto;

import java.io.Serializable;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

@Getter
public abstract sealed class BaseResponse implements Serializable
        permits ApiPlainResponse, ApiErrorResponse {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private final Instant timestamp;

    protected BaseResponse(Instant timestamp) {
        this.timestamp = timestamp;
    }

}
