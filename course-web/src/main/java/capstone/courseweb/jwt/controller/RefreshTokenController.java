package capstone.courseweb.jwt.controller;

import capstone.courseweb.jwt.JwtDto;
import capstone.courseweb.jwt.config.JwtAuthProvider;
import capstone.courseweb.jwt.utility.JwtIssuer;
import capstone.courseweb.user.domain.SignUpForm;
import capstone.courseweb.user.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class RefreshTokenController {
    private final JwtAuthProvider jwtAuthProvider;
    private final JwtIssuer jwtIssuer;
    private final MemberRepository memberRepository;

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtDto> refreshToken(@RequestBody JwtDto jwtDto) {
        //JWT Dto로 accesstoken과 refreshtoken 검증
        if (!jwtAuthProvider.validateToken(jwtDto)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Claims accessClaims = jwtIssuer.getClaims(jwtDto.getAccessToken());
        String userId = accessClaims.get("id", String.class);
        //둘 다 유효하면 새 토큰 생성
        String newAccessToken = String.valueOf(jwtIssuer.createToken(
                accessClaims.get("id", String.class),
                accessClaims.get("name", String.class),
                accessClaims.get("nickname", String.class)
        ));

        JwtDto newTokens = JwtDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(jwtDto.getRefreshToken()) // 기존의 RefreshToken을 그대로 사용
                .build();

        //리프레시토큰을 새로 발급하는 경우에 하면 됨.
        /*Optional<Member> memberOpt = memberRepository.findById(userId);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            member.setRefresh_token(newTokens.getRefreshToken());
            memberRepository.save(member);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }*/

        return ResponseEntity.ok(newTokens);
    }
}
