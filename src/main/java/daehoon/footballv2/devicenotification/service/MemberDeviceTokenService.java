package daehoon.footballv2.devicenotification.service;

import daehoon.footballv2.devicenotification.dto.request.MemberDeviceTokenRegisterRequest;

import java.util.List;

public interface MemberDeviceTokenService {

    void registerDeviceToken(Long memberId, MemberDeviceTokenRegisterRequest request);

    void unregisterDeviceToken(Long memberId, String token);

    List<String> findTokensByMemberId(Long memberId);
}
