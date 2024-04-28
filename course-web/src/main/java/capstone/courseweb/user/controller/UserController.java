package capstone.courseweb.user.controller;

import capstone.courseweb.jwt.JwtDto;
import capstone.courseweb.jwt.JwtIssuer;
import capstone.courseweb.user.domain.Member;
import capstone.courseweb.user.domain.SignUpForm;
import capstone.courseweb.user.service.MemberService;
import capstone.courseweb.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    @Autowired
    UserService userService;

    private final JwtIssuer jwtIssuer;
    private final MemberService memberService;

    @GetMapping("/user/callback/kakao")
    public ResponseEntity<JwtDto> kakaoLogin(@RequestParam("code") String code) throws JsonProcessingException {
        SignUpForm kakaoUserForm = userService.getUserInfo(code);
        log.info("Email: {}, ID: {}, Name: {}, Provider: {}", kakaoUserForm.getEmail(), kakaoUserForm.getId(), kakaoUserForm.getName(), kakaoUserForm.getProvider());
        memberService.signUp(kakaoUserForm);

        //jwt token 생성 후 kakaoJwtToken에 저장
        JwtDto kakaoJwtToken = jwtIssuer.createToken(kakaoUserForm.getId(), kakaoUserForm.getName(), Member.MemberRole.USER.name());
        log.info("access token: {}, refresh token: {}", kakaoJwtToken.getAccessToken(), kakaoJwtToken.getRefreshToken());
        return ResponseEntity.ok(kakaoJwtToken);
    }

}
