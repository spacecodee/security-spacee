package com.spacecodee.securityspacee.shared.adapter.in.web.dto;

import java.time.Instant;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;

public final class ResponseBuilder {

    private ResponseBuilder() {
        throw new AssertionError("Utility class, cannot be instantiated");
    }

    @Contract("_, _ -> new")
    public static <T> @NonNull ApiDataResponse<T> success(String message, T data) {
        return ApiDataResponse.success(message, data, Instant.now());
    }

    @Contract("_, _ -> new")
    public static <T> @NonNull ApiDataResponse<T> created(String message, T data) {
        return ApiDataResponse.created(message, data, Instant.now());
    }

    @Contract("_ -> new")
    public static @NonNull ApiPlainResponse successPlain(String message) {
        return ApiPlainResponse.success(message, Instant.now());
    }

    @Contract(" -> new")
    public static @NonNull ApiPlainResponse noContent() {
        return ApiPlainResponse.noContent(Instant.now());
    }

    public static <T> @NonNull ApiDataResponse<ApiPaginatedResponse<T>> successPaginated(String message, @NonNull Page<T> page) {
        PaginationInfo paginationInfo = PaginationInfo.builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();

        ApiPaginatedResponse<T> paginatedResponse = ApiPaginatedResponse.<T>builder()
                .pageData(page.getContent())
                .pagination(paginationInfo)
                .build();

        return ApiDataResponse.success(message, paginatedResponse, Instant.now());
    }

    @Contract("_, _, _ -> new")
    public static @NonNull ApiErrorResponse badRequest(String message, String path, String method) {
        return ApiErrorResponse.badRequest(message, path, method, Instant.now());
    }

    @Contract("_, _, _, _ -> new")
    public static <E> @NonNull ApiErrorDataResponse<E> badRequestWithDetails(String message, String path, String method,
                                                                             E details) {
        return ApiErrorDataResponse.badRequest(message, path, method, Instant.now(), details);
    }

    @Contract("_, _, _ -> new")
    public static @NonNull ApiErrorResponse unauthorized(String message, String path, String method) {
        return ApiErrorResponse.unauthorized(message, path, method, Instant.now());
    }

    @Contract("_, _, _ -> new")
    public static @NonNull ApiErrorResponse forbidden(String message, String path, String method) {
        return ApiErrorResponse.forbidden(message, path, method, Instant.now());
    }

    @Contract("_, _, _ -> new")
    public static @NonNull ApiErrorResponse notFound(String message, String path, String method) {
        return ApiErrorResponse.notFound(message, path, method, Instant.now());
    }

    @Contract("_, _, _ -> new")
    public static @NonNull ApiErrorResponse conflict(String message, String path, String method) {
        return ApiErrorResponse.conflict(message, path, method, Instant.now());
    }

    @Contract("_, _, _, _ -> new")
    public static <E> @NonNull ApiErrorDataResponse<E> conflictWithDetails(String message, String path, String method,
                                                                           E details) {
        return ApiErrorDataResponse.conflict(message, path, method, Instant.now(), details);
    }

    @Contract("_, _, _ -> new")
    public static @NonNull ApiErrorResponse locked(String message, String path, String method) {
        return ApiErrorResponse.locked(message, path, method, Instant.now());
    }

    @Contract("_, _, _, _ -> new")
    public static <E> @NonNull ApiErrorDataResponse<E> validationError(String message, String path, String method,
                                                                       E validationErrorsData) {
        return ApiErrorDataResponse.validationError(message, path, method, Instant.now(), validationErrorsData);
    }

    @Contract("_, _, _ -> new")
    public static @NonNull ApiErrorResponse internalError(String message, String path, String method) {
        return ApiErrorResponse.internalError(message, path, method, Instant.now());
    }

}
