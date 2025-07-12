package ru.ntwz.javaspringbootauthexample.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.ntwz.javaspringbootauthexample.constant.AuthenticationConstant;
import ru.ntwz.javaspringbootauthexample.dto.response.ApiError;
import ru.ntwz.javaspringbootauthexample.exception.NotAuthorizedException;
import ru.ntwz.javaspringbootauthexample.service.JwtService;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
public class AuthorizationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final ObjectMapper mapper;

    @Autowired
    public AuthorizationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;

        this.mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(AuthenticationConstant.HEADER_NAME);

        if (StringUtils.isEmpty(authorizationHeader) || !authorizationHeader.startsWith(AuthenticationConstant.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authorizationHeader.substring(AuthenticationConstant.TOKEN_PREFIX.length());
        Long userId;

        try {
            userId = jwtService.extractUserId(jwt);

            if (userId == null) {
                throw new NotAuthorizedException("Invalid JWT token. User ID is null.");
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserById(userId);

                if (!jwtService.validateToken(jwt, userDetails)) {
                    throw new NotAuthorizedException("Invalid JWT token.");
                }

                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                securityContext.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(securityContext);
            }
            filterChain.doFilter(request, response);
        } catch (NotAuthorizedException ex) {
            ApiError apiError = new ApiError();
            apiError.setErrors(Collections.singletonList(ex.toString()));
            apiError.setReason("Not authorized to access this resource");
            apiError.setStatus(HttpStatus.UNAUTHORIZED.name());
            apiError.setMessage(ex.getMessage());

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            try {
                response.getWriter().write(mapper.writeValueAsString(apiError));
            } catch (IOException e) {
                log.error("Error writing response: {}", e.getMessage());
            }
        }
    }
}
