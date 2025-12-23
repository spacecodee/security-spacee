package com.spacecodee.securityspacee.jwttoken.application.response;

import java.util.List;

import lombok.Builder;

@Builder
public record UserRevocationSummary(Integer tokensRevokedCount, Integer sessionsAffectedCount,
                                    List<String> affectedSessions) {
}
