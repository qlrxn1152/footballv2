package daehoon.footballv2.devicenotification.service;

import daehoon.footballv2.devicenotification.dto.request.FcmPushRequest;

public interface FcmPushService {

    void sendToMember(Long memberId, FcmPushRequest request);
}
