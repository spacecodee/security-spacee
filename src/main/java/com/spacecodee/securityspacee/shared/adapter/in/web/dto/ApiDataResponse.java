package com.spacecodee.securityspacee.shared.adapter.in.web.dto;

import java.io.Serial;
import java.time.Instant;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public final class ApiDataResponse<T> extends ApiPlainResponse {

    @Serial
    private static final long serialVersionUID = 1L;

    private final transient T data;

    private ApiDataResponse(String message, HttpStatus httpStatus, T data, Instant timestamp) {
        super(message, httpStatus, timestamp);
        this.data = data;
    }

    @Contract("_, _, _, _ -> new")
    public static <T> @NonNull ApiDataResponse<T> of(String message, HttpStatus status, T data, Instant timestamp) {
        return new ApiDataResponse<>(message, status, data, timestamp);
    }

    @Contract("_, _, _ -> new")
    public static <T> @NonNull ApiDataResponse<T> success(String message, T data, Instant timestamp) {
        return new ApiDataResponse<>(message, HttpStatus.OK, data, timestamp);
    }

    @Contract("_, _, _ -> new")
    public static <T> @NonNull ApiDataResponse<T> created(String message, T data, Instant timestamp) {
        return new ApiDataResponse<>(message, HttpStatus.CREATED, data, timestamp);
    }

}
