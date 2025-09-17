package info.sec.lab1.authentication.jwt;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String accessToken;
    private final String refreshToken;
    private final String principal;

    public JwtAuthenticationToken(String accessToken, String refreshToken, String principal) {
        this(accessToken, refreshToken, principal, Collections.emptyList());
    }

    public JwtAuthenticationToken(String accessToken, String refreshToken, String principal,
                                  Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.principal = principal;
    }

    @Override
    public Object getCredentials() {
        return accessToken;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
