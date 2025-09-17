package info.sec.lab1.authentication;

import java.util.List;

public class Constants {
    public static final String LOGIN_URL = "/auth/login";
    public static final String REGISTER_URL = "/auth/register";
    public static final String REFRESH_TOKEN_URL = "/auth/refresh-token";
    public static final List<String> URLS_WITHOUT_JWT_AUTHORIZATION = List.of(LOGIN_URL, REFRESH_TOKEN_URL, REGISTER_URL);
}
