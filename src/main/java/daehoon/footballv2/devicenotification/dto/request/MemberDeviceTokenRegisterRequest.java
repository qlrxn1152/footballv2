package daehoon.footballv2.devicenotification.dto.request;

import daehoon.footballv2.devicenotification.domain.DevicePlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class MemberDeviceTokenRegisterRequest {

    @NotBlank(message = "기기 토큰은 필수입니다.")
    @Size(max = 512, message = "기기 토큰은 512자를 초과할 수 없습니다.")
    private String token;

    @NotNull(message = "기기 플랫폼은 필수입니다.")
    private DevicePlatform platform;

}
