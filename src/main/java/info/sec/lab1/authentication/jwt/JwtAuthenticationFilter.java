package info.sec.lab1.authentication.jwt;

import info.sec.lab1.authentication.CustomAuthenticationManagerResolver;
import info.sec.lab1.authentication.Constants;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationFilter extends AuthenticationFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    public JwtAuthenticationFilter(CustomAuthenticationManagerResolver authenticationManagerResolver) {
        super(authenticationManagerResolver, new JwtAuthenticationConverter());
    }

    @PostConstruct
    public void setup() {
        setRequestMatcher(request -> {
            String authHeader = request.getHeader(AUTHORIZATION_HEADER);
            return !Constants.URLS_WITHOUT_JWT_AUTHORIZATION.contains(request.getRequestURI()) &&
                    authHeader != null && authHeader.startsWith(BEARER_PREFIX);
        });

        setSuccessHandler(
                (request, response, authentication) -> SecurityContextHolder.getContext().setAuthentication(authentication)
        );
    }
}
