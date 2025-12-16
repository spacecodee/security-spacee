package com.spacecodee.securityspacee.shared.adapter.in.web.dto;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;

@Builder
@JsonPropertyOrder({ "page", "size", "totalPages", "totalElements", "first", "last", "hasNext", "hasPrevious" })
public record PaginationInfo(
        int page,
        int size,
        int totalPages,
        long totalElements,
        boolean first,
        boolean last,
        boolean hasNext,
        boolean hasPrevious) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}
