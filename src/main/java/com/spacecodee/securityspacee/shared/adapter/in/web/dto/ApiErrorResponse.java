package com.spacecodee.securityspacee.shared.adapter.in.web.dto;

import java.time.Instant;

import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

@Getter
public non-sealed class ApiErrorResponse extends BaseResponse {

    private final String message;
    private final String path;
    private final String method;
    private final int status;

    protected ApiErrorResponse(String message, String path, String method, int status, Instant timestamp) {
        super(timestamp);
        this.message = message;
        this.path = path;
        this.method = method;
        this.status = status;
    }

    @Contract("_, _, _, _, _ -> new")
    public static @NonNull ApiErrorResponse of(String message, String path, String method, int status, Instant timestamp) {
        return new ApiErrorResponse(message, path, method, status, timestamp);
    }

    @Contract("_, _, _, _ -> new")
    public static @NonNull ApiErrorResponse badRequest(String message, String path, String method, Instant timestamp) {
        return new ApiErrorResponse(message, path, method, 400, timestamp);
    }

    @Contract("_, _, _, _ -> new")
    public static @NonNull ApiErrorResponse unauthorized(String message, String path, String method, Instant timestamp) {
        return new ApiErrorResponse(message, path, method, 401, timestamp);
    }

    @Contract("_, _, _, _ -> new")
    public static @NonNull ApiErrorResponse forbidden(String message, String path, String method, Instant timestamp) {
        return new ApiErrorResponse(message, path, method, 403, timestamp);
    }

    @Contract("_, _, _, _ -> new")
    public static @NonNull ApiErrorResponse notFound(String message, String path, String method, Instant timestamp) {
        return new ApiErrorResponse(message, path, method, 404, timestamp);
    }

    @Contract("_, _, _, _ -> new")
    public static @NonNull ApiErrorResponse conflict(String message, String path, String method, Instant timestamp) {
        return new ApiErrorResponse(message, path, method, 409, timestamp);
    }

    @Contract("_, _, _, _ -> new")
    public static @NonNull ApiErrorResponse locked(String message, String path, String method, Instant timestamp) {
        return new ApiErrorResponse(message, path, method, 423, timestamp);
    }

    @Contract("_, _, _, _ -> new")
    public static @NonNull ApiErrorResponse unprocessableEntity(String message, String path, String method, Instant timestamp) {
        return new ApiErrorResponse(message, path, method, 422, timestamp);
    }

    @Contract("_, _, _, _ -> new")
    public static @NonNull ApiErrorResponse internalError(String message, String path, String method, Instant timestamp) {
        return new ApiErrorResponse(message, path, method, 500, timestamp);
    }

}
