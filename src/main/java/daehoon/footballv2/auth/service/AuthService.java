package daehoon.footballv2.auth.service;

import daehoon.footballv2.auth.dto.LoginResponse;
import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.auth.dto.SignupResponse;

public interface AuthService {

    SignupResponse signup(String username, String password);

    LoginResponse login(String username, String password);
}
