package info.sec.lab1.controller;

import info.sec.lab1.authentication.Constants;
import info.sec.lab1.authentication.jwt.JwtAuthenticationToken;
import info.sec.lab1.dto.JwtResponse;
import info.sec.lab1.dto.LoginRequest;
import info.sec.lab1.dto.RefreshRequest;
import info.sec.lab1.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(Constants.LOGIN_URL)
    public JwtResponse login(@RequestBody LoginRequest request) throws Exception {
        JwtAuthenticationToken jwtAuthenticationToken = authService.loginByUsernameAndPassword(
                request.getUsername(),
                request.getPassword()
        );
        return new JwtResponse(
                jwtAuthenticationToken.getAccessToken(),
                jwtAuthenticationToken.getRefreshToken()
        );
    }

    @PostMapping(Constants.REGISTER_URL)
    public void register(@RequestBody LoginRequest request) {
        authService.registerUser(request.getUsername(), request.getPassword());
    }

    @PostMapping(Constants.REFRESH_TOKEN_URL)
    public JwtResponse refreshToken(@RequestBody RefreshRequest request) {
        JwtAuthenticationToken jwtAuthenticationToken = authService.refreshJwtAuthenticationToken(request.getRefreshToken());
        return new JwtResponse(
                jwtAuthenticationToken.getAccessToken(),
                jwtAuthenticationToken.getRefreshToken()
        );
    }
}
