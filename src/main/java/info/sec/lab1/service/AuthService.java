//package info.sec.lab1.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class AuthService(
//        private val authenticationManager: AuthenticationManager,
//        private val jwtService: JwtService,
//        private val userDetailsService: UserDetailsService
//) {
//    fun loginByUsernameAndPassword(username: String, password: String): JwtAuthenticationToken {
//        val authenticationToken = UsernamePasswordAuthenticationToken(username, password)
//        val authentication = authenticationManager.authenticate(authenticationToken)
//        val userDetails = authentication.principal as UserDetails
//        return jwtService.generateJwtAuthenticationToken(userDetails)
//    }
//
//    fun refreshJwtAuthenticationToken(refreshToken: String): JwtAuthenticationToken {
//        if (jwtService.isTokenExpired(refreshToken, JwtTokenType.REFRESH)) {
//            throw BadCredentialsException("Expired access token")
//        }
//        val username = jwtService.extractUsername(refreshToken, JwtTokenType.REFRESH)
//        val userDetails = userDetailsService.loadUserByUsername(username)
//
//        val isValid = jwtService.isTokenValid(refreshToken, JwtTokenType.REFRESH, userDetails)
//        if (!isValid) {
//            throw BadCredentialsException("Invalid refresh token")
//        }
//        return jwtService.generateJwtAuthenticationToken(userDetails)
//    }
//}
package info.sec.lab1.service;

import info.sec.lab1.authentication.CustomAuthenticationManagerResolver;
import info.sec.lab1.authentication.jwt.JwtAuthenticationToken;
import info.sec.lab1.authentication.jwt.JwtService;
import info.sec.lab1.authentication.jwt.JwtTokenType;
import info.sec.lab1.entity.Role;
import info.sec.lab1.entity.User;
import info.sec.lab1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomAuthenticationManagerResolver managerResolver;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public JwtAuthenticationToken loginByUsernameAndPassword(String username, String password) throws Exception {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        Authentication authentication =
                managerResolver.getAuthenticationManager().authenticate(authenticationToken);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return jwtService.generateJwtAuthenticationToken(userDetails);
    }

    public User getCurrentUser() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (User) userDetailsService.loadUserByUsername(username);
    }

    public void registerUser(String username, String password) {
        userRepository.findUserByUsername(username).ifPresent(
                user -> {throw new IllegalArgumentException("User '%s' already exists".formatted(username));}
        );
        userRepository.save(new User(username, passwordEncoder.encode(password), Set.of(Role.USER)));
    }

    public JwtAuthenticationToken refreshJwtAuthenticationToken(String refreshToken) {
        if (jwtService.isTokenExpired(refreshToken, JwtTokenType.REFRESH)) {
            throw new BadCredentialsException("Expired refresh token");
        }

        String username = jwtService.extractUsername(refreshToken, JwtTokenType.REFRESH);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        boolean isValid = jwtService.isTokenValid(refreshToken, JwtTokenType.REFRESH, userDetails);
        if (!isValid) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        return jwtService.generateJwtAuthenticationToken(userDetails);
    }
}
