package daehoon.footballv2.auth.service;

import daehoon.footballv2.auth.dto.response.login.LoginResponse;
import daehoon.footballv2.auth.dto.response.signup.SignupResponse;

public interface AuthService {

    SignupResponse signup(String username, String password);

    LoginResponse login(String username, String password);
}
