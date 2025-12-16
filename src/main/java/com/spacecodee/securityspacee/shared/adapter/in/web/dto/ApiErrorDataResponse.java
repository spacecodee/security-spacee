package com.spacecodee.securityspacee.shared.adapter.in.web.dto;

import java.time.Instant;

import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

@Getter
public final class ApiErrorDataResponse<E> extends ApiErrorResponse {

    private final transient E data;

    private ApiErrorDataResponse(String message, String path, String method, int status, Instant timestamp, E data) {
        super(message, path, method, status, timestamp);
        this.data = data;
    }

    @Contract("_, _, _, _, _, _ -> new")
    public static <E> @NonNull ApiErrorDataResponse<E> of(String message, String path, String method, int status,
                                                          Instant timestamp, E data) {
        return new ApiErrorDataResponse<>(message, path, method, status, timestamp, data);
    }

    @Contract("_, _, _, _, _ -> new")
    public static <E> @NonNull ApiErrorDataResponse<E> badRequest(String message, String path, String method, Instant timestamp,
                                                                  E data) {
        return new ApiErrorDataResponse<>(message, path, method, 400, timestamp, data);
    }

    @Contract("_, _, _, _, _ -> new")
    public static <E> @NonNull ApiErrorDataResponse<E> validationError(String message, String path, String method,
                                                                       Instant timestamp, E validationErrorsData) {
        return new ApiErrorDataResponse<>(message, path, method, 422, timestamp, validationErrorsData);
    }

    @Contract("_, _, _, _, _ -> new")
    public static <E> @NonNull ApiErrorDataResponse<E> conflict(String message, String path, String method, Instant timestamp,
                                                                E data) {
        return new ApiErrorDataResponse<>(message, path, method, 409, timestamp, data);
    }

}
