package capstone.courseweb.user.controller;

import capstone.courseweb.jwt.JwtDto;
import capstone.courseweb.jwt.utility.JwtIssuer;
import capstone.courseweb.user.domain.Member;
import capstone.courseweb.user.domain.SignUpForm;
import capstone.courseweb.user.repository.MemberRepository;
import capstone.courseweb.user.service.MemberService;
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

    private SignUpForm kakaoUserForm;
    private final MemberRepository memberRepository;
    @Autowired
    UserService userService;

    private final JwtIssuer jwtIssuer;
    private final MemberService memberService;

    @GetMapping("/user/callback/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestParam("code") String code) throws JsonProcessingException {

        System.out.println("프론트에서 받은 인가 코드" + code);

        kakaoUserForm = userService.getUserInfo(code);
        log.info("Email: {}, ID: {}, Name: {}, Provider: {}", kakaoUserForm.getEmail(), kakaoUserForm.getId(), kakaoUserForm.getName());

        Optional<Member> memberOpt = memberRepository.findById(kakaoUserForm.getId());

        if (memberOpt.isPresent()) { //db에 회원정보 있을 때
            Member user = memberOpt.get();
            if (user.getNickname()==null) { // 닉네임 없으면 닉네임 화면으로
                Map<String, Object> response = new HashMap<>();
                response.put("status", HttpStatus.OK.value());
                response.put("message", "닉네임 없음");
                return ResponseEntity.ok(response);
            } else { //닉네임 있으면 유저벡터 있는지 확인
                if (user.getUser_vector()==null) { //유저벡터 없으면 선호도 테스트 화면으로
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", HttpStatus.OK.value());
                    response.put("nickname", user.getNickname());
                    response.put("message", "선호도 테스트");
                    log.info("닉네임 보내기: {}", user.getNickname());
                    return ResponseEntity.ok(response);
                } else { //유저벡터까지 있으면 회원가입 완료 -> 홈화면
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", HttpStatus.OK.value());
                    response.put("nickname", user.getNickname());
                    response.put("message", "홈화면");
                    return ResponseEntity.ok(response);
                }
            }

        }
        else { //db에 회원정보 없을 때
            JwtDto kakaoJwtToken = jwtIssuer.createToken(kakaoUserForm.getId(), kakaoUserForm.getName());
            kakaoUserForm.setRefresh_token(kakaoJwtToken.getRefreshToken());
            memberService.signUp(kakaoUserForm);

            /**프론트랑 연결해보려고 return 값 바꿈**/
            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.CREATED.value());
            response.put("token", kakaoJwtToken);
            return ResponseEntity.ok(response);
            //return ResponseEntity.ok(kakaoJwtToken);
        }
    }

    /**닉네임 중복 추가**/
    @PostMapping("/user/nickname")
    public ResponseEntity<?> nicknameCheck(@RequestBody NicknameController nicknameRequest) {

        // JWT 토큰 검증
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid JWT token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        log.info("authentication.getPrincipal값 확인: {]", authentication.getPrincipal());

        //사용자 정보 가져오기
        Member member = (Member) authentication.getPrincipal();
        String id = member.getId();
        Optional<Member> memberOpt = memberRepository.findById(id);
        if (memberOpt.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        

        String nickname = nicknameRequest.getNickname();
        log.info("닉네임: " + nickname);
        if (memberRepository.existsByNickname(nickname)) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.OK.value());
            response.put("message", "닉네임 중복");
            return ResponseEntity.ok(response);
        } else { //닉네임 중복 아닐 때
            kakaoUserForm.setNickname(nickname);

            //닉네임 저장
            Member user = memberOpt.get();
            user.setNickname(nickname);
            log.info("memberOpt.get().get 작동 확인: {}", memberOpt.get().getName());
            memberRepository.save(user);
            log.info("유저 컨트롤러 nickname 출력: {}", nickname);

            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.OK.value());
            response.put("message", "닉네임 사용 가능");
            return ResponseEntity.ok(response);
        }
    }

}