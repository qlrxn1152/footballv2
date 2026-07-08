package daehoon.footballv2.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class LoginRequest {

    @NotBlank
    @Size(min = 4, max = 30, message = "아이디는 4~30글자 사이여야 합니다.")
    private String username;

    @NotBlank
    @Size(min = 8, max = 30, message = "message = 비밀번호는 8글자 이상 30자 이하여야합니다.")
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
