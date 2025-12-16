package com.spacecodee.securityspacee.shared.adapter.in.web.dto;

import java.time.Instant;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public non-sealed class ApiPlainResponse extends BaseResponse {

    private final String message;
    private final HttpStatus httpStatus;

    protected ApiPlainResponse(String message, HttpStatus httpStatus, Instant timestamp) {
        super(timestamp);
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Contract("_, _, _ -> new")
    public static @NonNull ApiPlainResponse of(String message, HttpStatus status, Instant timestamp) {
        return new ApiPlainResponse(message, status, timestamp);
    }

    @Contract("_, _ -> new")
    public static @NonNull ApiPlainResponse success(String message, Instant timestamp) {
        return new ApiPlainResponse(message, HttpStatus.OK, timestamp);
    }

    @Contract("_, _ -> new")
    public static @NonNull ApiPlainResponse created(String message, Instant timestamp) {
        return new ApiPlainResponse(message, HttpStatus.CREATED, timestamp);
    }

    @Contract("_ -> new")
    public static @NonNull ApiPlainResponse noContent(Instant timestamp) {
        return new ApiPlainResponse("No Content", HttpStatus.NO_CONTENT, timestamp);
    }

}
