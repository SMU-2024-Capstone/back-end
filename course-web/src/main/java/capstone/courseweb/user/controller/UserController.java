package capstone.courseweb.user.controller;

import capstone.courseweb.jwt.JwtDto;
import capstone.courseweb.jwt.service.AuthService;
import capstone.courseweb.jwt.utility.JwtIssuer;
import capstone.courseweb.user.domain.Member;
import capstone.courseweb.user.domain.SignUpForm;
import capstone.courseweb.user.repository.MemberRepository;
import capstone.courseweb.user.service.MemberService;
import capstone.courseweb.user.service.NicknameService;
import capstone.courseweb.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {


    private final UserService userService;
    private final NicknameService nicknameService;
    private final AuthService authService;

    @GetMapping("/user/callback/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestParam("code") String code) throws JsonProcessingException {

        System.out.println("프론트에서 받은 인가 코드" + code);
        return ResponseEntity.ok(userService.handleKakaoLogin(code));


    }

    /**닉네임 중복 추가**/
    @PostMapping("/user/nickname")
    public ResponseEntity<?> nicknameCheck(@RequestBody  Map<String, String> request) {

        Member member = authService.getAuthenticatedMember();
        String id = member.getId();
        log.info("id 잘 추출됐는지 확인:{}", id);

        String nickname = request.get("nickname");
        log.info("닉네임: " + nickname);

         return ResponseEntity.ok(nicknameService.checkAndSetNickname(nickname, id));
    }

}