package ua.nure.moleculis.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ua.nure.moleculis.exception.CustomException;
import ua.nure.moleculis.exception.ErrorResponse;
import ua.nure.moleculis.repos.TokenRepo;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtTokenFilter extends OncePerRequestFilter {

    private JwtTokenProvider jwtTokenProvider;

    private final TokenRepo tokenRepo;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider, TokenRepo tokenRepo) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenRepo = tokenRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        try {
            if (tokenRepo.existsByToken(token)) {
                throw new CustomException("User logged out", HttpStatus.UNAUTHORIZED);
            }
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (CustomException ex) {
            SecurityContextHolder.clearContext();
            httpServletResponse.setStatus(ex.getHttpStatus().value());
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.getWriter().write(convertObjectToJson(new ErrorResponse(ex.getMessage(), ex.getHttpStatus().value())));
            return;
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    public String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

}
