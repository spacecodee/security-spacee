package com.spacecodee.securityspacee.shared.adapter.in.web.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;

@Builder
@JsonPropertyOrder({ "pageData", "pagination" })
public record ApiPaginatedResponse<T>(
        List<T> pageData,
        PaginationInfo pagination) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}
