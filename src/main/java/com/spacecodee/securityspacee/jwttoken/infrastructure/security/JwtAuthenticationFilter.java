package com.spacecodee.securityspacee.jwttoken.infrastructure.security;

import com.spacecodee.securityspacee.jwttoken.application.command.ValidateTokenCommand;
import com.spacecodee.securityspacee.jwttoken.application.port.in.IValidateTokenUseCase;
import com.spacecodee.securityspacee.jwttoken.application.response.TokenValidationResponse;
import com.spacecodee.securityspacee.jwttoken.domain.exception.InvalidSignatureException;
import com.spacecodee.securityspacee.jwttoken.domain.exception.InvalidTokenException;
import com.spacecodee.securityspacee.jwttoken.domain.exception.TokenExpiredException;
import com.spacecodee.securityspacee.jwttoken.domain.exception.TokenRevokedException;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.ValidationMode;
import com.spacecodee.securityspacee.jwttoken.infrastructure.security.mapper.ISecurityAuthenticationMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public final class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private final IValidateTokenUseCase validateTokenUseCase;
    private final ISecurityAuthenticationMapper securityAuthenticationMapper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException {

        try {
            String token = this.extractToken(request);

            if (token != null) {
                ValidateTokenCommand command = new ValidateTokenCommand(token, ValidationMode.FAST);
                TokenValidationResponse validationResult = this.validateTokenUseCase.execute(command);

                if (validationResult.valid()) {
                    Authentication authentication = this.securityAuthenticationMapper
                            .toAuthentication(validationResult);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    request.setAttribute("userId", validationResult.userId());
                    request.setAttribute("sessionId", validationResult.sessionId());
                }
            }

            filterChain.doFilter(request, response);

        } catch (InvalidSignatureException | InvalidTokenException | TokenExpiredException | TokenRevokedException ex) {
            log.debug("JWT validation failed: {}", ex.getMessage());
            throw new BadCredentialsException(ex.getMessage(), ex);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error in JWT filter", ex);
            throw new ServletException("JWT processing failed", ex);
        }
    }

    private @Nullable String extractToken(@NonNull HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }

        return null;
    }

}
