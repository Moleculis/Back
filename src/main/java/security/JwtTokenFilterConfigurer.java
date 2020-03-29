package security;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import repos.TokenRepo;

public class JwtTokenFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private JwtTokenProvider jwtTokenProvider;

    private final TokenRepo tokenRepo;

    public JwtTokenFilterConfigurer(JwtTokenProvider jwtTokenProvider, TokenRepo tokenRepo) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenRepo = tokenRepo;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        JwtTokenFilter customFilter = new JwtTokenFilter(jwtTokenProvider, tokenRepo);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
