package info.sec.lab1.authentication.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) {
        JwtAuthenticationToken jwtAuthToken = (JwtAuthenticationToken) authentication;

        String accessToken = jwtAuthToken.getAccessToken();
        if (jwtService.isTokenExpired(accessToken, JwtTokenType.ACCESS)) {
            throw new BadCredentialsException("Expired access token");
        }

        String username = jwtService.extractUsername(jwtAuthToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails == null) {
            throw new UsernameNotFoundException("User " + username + " not found");
        }

        if (!jwtService.isTokenValid(accessToken, JwtTokenType.ACCESS, userDetails)) {
            throw new BadCredentialsException("Invalid or expired access token");
        }

        JwtAuthenticationToken authenticatedToken = new JwtAuthenticationToken(
                accessToken,
                jwtAuthToken.getRefreshToken(),
                userDetails.getUsername(),
                userDetails.getAuthorities()
        );
        authenticatedToken.setAuthenticated(true);
        return authenticatedToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
