package capstone.courseweb.user.controller;

import capstone.courseweb.jwt.service.AuthService;
import capstone.courseweb.user.domain.Member;
import capstone.courseweb.user.service.NicknameService;
import capstone.courseweb.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final NicknameService nicknameService;
    private final AuthService authService;

    @GetMapping("/user/callback/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestParam("code") String code) throws JsonProcessingException {
        return ResponseEntity.ok(userService.handleKakaoLogin(code));


    }

    /**닉네임 중복 추가**/
    @PostMapping("/user/nickname")
    public ResponseEntity<?> nicknameCheck(@RequestBody  Map<String, String> request) {

        Member member = authService.getAuthenticatedMember();
        String id = member.getId();
        String nickname = request.get("nickname");

         return ResponseEntity.ok(nicknameService.checkAndSetNickname(nickname, id));
    }

}