package capstone.courseweb.user.controller;

import capstone.courseweb.jwt.JwtDto;
import capstone.courseweb.jwt.utility.JwtIssuer;
import capstone.courseweb.user.domain.SignUpForm;
import capstone.courseweb.user.repository.MemberRepository;
import capstone.courseweb.user.service.MemberService;
import capstone.courseweb.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    private SignUpForm kakaoUserForm;
    private final MemberRepository memberRepository;
    @Autowired
    UserService userService;

    private final JwtIssuer jwtIssuer;
    private final MemberService memberService;

    @GetMapping("/user/callback/kakao")
    public ResponseEntity<String> kakaoLogin(@RequestParam("code") String code) throws JsonProcessingException {

        System.out.println(code);
        kakaoUserForm = userService.getUserInfo(code);
        log.info("Email: {}, ID: {}, Name: {}, Provider: {}", kakaoUserForm.getEmail(), kakaoUserForm.getId(), kakaoUserForm.getName()); //, kakaoUserForm.getProvider()

        if (memberRepository.existsById(kakaoUserForm.getId())) { //db에 회원정보 있을 때
            return ResponseEntity.ok("로그인");
        }
        else { //db에 회원정보 없을 때
            return ResponseEntity.ok("회원가입. 닉네임 설정으로 이동");
        }
    }

    /**닉네임 중복 추가**/
    @PostMapping("/user/nickname")
    public ResponseEntity<?> nicknameCheck(@RequestBody NicknameController nicknameRequest) {
        String nickname = nicknameRequest.getNickname();
        log.info("닉네임: " + nickname);
        if (memberRepository.existsByNickname(nickname)) {
            log.info("닉네임 중복");
            return ResponseEntity.ok("닉네임 중복");
        } else { //닉네임 중복 아닐 때
            kakaoUserForm.setNickname(nickname);
            //카카오 아이디, 카카오 닉네임, 이길로 닉네임으로 jwt 토큰 생성
            JwtDto kakaoJwtToken = jwtIssuer.createToken(kakaoUserForm.getId(), kakaoUserForm.getName(), kakaoUserForm.getNickname());
            kakaoUserForm.setRefresh_token(kakaoJwtToken.getRefreshToken());
            memberService.signUp(kakaoUserForm);
            log.info("닉네임 저장");
            log.info("access token: {}, refresh token: {}", kakaoJwtToken.getAccessToken(), kakaoJwtToken.getRefreshToken());
            return ResponseEntity.ok(kakaoJwtToken);
        }
    }

}