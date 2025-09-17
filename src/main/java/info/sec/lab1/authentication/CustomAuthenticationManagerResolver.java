package info.sec.lab1.authentication;

import info.sec.lab1.authentication.jwt.JwtAuthenticationProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component("authenticationManagerResolver")
@RequiredArgsConstructor
public class CustomAuthenticationManagerResolver implements AuthenticationManagerResolver<HttpServletRequest> {

    private final JwtAuthenticationProvider jwtAuthProvider;
    private final DaoAuthenticationProvider daoAuthProvider;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Override
    public AuthenticationManager resolve(HttpServletRequest context) {
        if (Constants.URLS_WITHOUT_JWT_AUTHORIZATION.contains(context.getRequestURI())) {
            return new ProviderManager(Collections.singletonList(daoAuthProvider));
        } else {
            return new ProviderManager(Collections.singletonList(jwtAuthProvider));
        }
    }

    public AuthenticationManager getAuthenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
