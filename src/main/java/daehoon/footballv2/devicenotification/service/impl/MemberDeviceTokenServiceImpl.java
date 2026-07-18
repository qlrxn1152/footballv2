package daehoon.footballv2.devicenotification.service.impl;

import daehoon.footballv2.devicenotification.domain.MemberDeviceToken;
import daehoon.footballv2.devicenotification.dto.request.MemberDeviceTokenRegisterRequest;
import daehoon.footballv2.devicenotification.exception.exceptions.NotFoundMemberDeviceTokenException;
import daehoon.footballv2.devicenotification.repository.MemberDeviceTokenRepository;
import daehoon.footballv2.devicenotification.service.MemberDeviceTokenService;
import daehoon.footballv2.member.domain.Member;
import daehoon.footballv2.team.validator.TeamValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MemberDeviceTokenServiceImpl implements MemberDeviceTokenService {

    private final MemberDeviceTokenRepository memberDeviceTokenRepository;

    private final TeamValidator teamValidator;

    @Override
    public void registerDeviceToken(Long memberId, MemberDeviceTokenRegisterRequest request) {
        Member member = teamValidator.validateMemberExists(memberId);

        // 토큰 없음 -> 새로 생성 // 있음 -> 갱신
        Optional<MemberDeviceToken> optToken = memberDeviceTokenRepository.findByToken(request.getToken());

        if (optToken.isPresent()) {
            optToken.get().refreshRegistration(member, request.getPlatform());
            return;
        }

        memberDeviceTokenRepository.save(new MemberDeviceToken(member, request.getToken(),  request.getPlatform()));
    }

    @Override
    public void unregisterDeviceToken(Long memberId, String token) {
        teamValidator.validateMemberExists(memberId);

        memberDeviceTokenRepository.deleteByMemberIdAndToken(memberId, token);
    }

    @Override
    public List<String> findTokensByMemberId(Long memberId) {
        return memberDeviceTokenRepository.findAllByMemberId(memberId)
                .stream()
                .map(MemberDeviceToken::getToken)
                .toList();
    }


}
