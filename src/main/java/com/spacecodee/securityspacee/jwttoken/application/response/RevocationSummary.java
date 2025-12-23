package com.spacecodee.securityspacee.jwttoken.application.response;

import java.util.List;

import lombok.Builder;

@Builder
public record RevocationSummary(Integer tokensRevokedCount, List<String> revokedJtis) {
}
