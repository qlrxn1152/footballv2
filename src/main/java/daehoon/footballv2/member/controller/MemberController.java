package daehoon.footballv2.member.controller;

import daehoon.footballv2.member.dto.response.MemberRankingResponse;
import daehoon.footballv2.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/api/members/ranking")
    public ResponseEntity<List<MemberRankingResponse>> memberRanking() {
        List<MemberRankingResponse> response = memberService.membersRanking();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
