package daehoon.footballv2.security.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginMemberPrincipal {

    private final Long memberId;
    private final String username;
}
