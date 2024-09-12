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

        if (memberRepository.existsById(kakaoUserForm.getId())) { //db에 회원정보 있을 때
            return ResponseEntity.ok("로그인");
        }
        else { //db에 회원정보 없을 때
            JwtDto kakaoJwtToken = jwtIssuer.createToken(kakaoUserForm.getId(), kakaoUserForm.getName());
            kakaoUserForm.setRefresh_token(kakaoJwtToken.getRefreshToken());
            //memberService.signUp(kakaoUserForm);

            return ResponseEntity.ok(kakaoJwtToken);
            //return ResponseEntity.ok("회원가입. 닉네임 설정으로 이동");
        }
    }

    /**닉네임 중복 추가**/
    @PostMapping("/user/nickname")
    public ResponseEntity<?> nicknameCheck(@RequestBody NicknameController nicknameRequest) {

        /*
        // JWT 토큰 검증
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid JWT token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

         */


        //닉네임 말고 id 받아오기
        /*System.out.println("1닉네임 컨트롤러 - authentication.getPrinciple: " + authentication.getPrincipal().toString());
        //System.out.println("2닉네임 컨트롤러 - authentication.getPrinciple: " + (Member) authentication.getPrincipal());
        System.out.println("3닉네임 컨트롤러 - authentication.getPrinciple: " + ((Member) authentication.getPrincipal()).getId());
        Member member = (Member) authentication.getPrincipal();
        String id = member.getId();
        System.out.println("서치 화면 jwt로 사용자 아이디 가져오기: " + id);


        Optional<Member> memberOpt = memberRepository.findById(id);
        if (memberOpt.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
*/

        String nickname = nicknameRequest.getNickname();
        log.info("닉네임: " + nickname);
        if (memberRepository.existsByNickname(nickname)) {
            log.info("닉네임 중복");
            return ResponseEntity.ok("닉네임 중복");
        } else { //닉네임 중복 아닐 때
            //System.out.println("kakaoUserForm에 소셜 id가 들어있는지 " + kakaoUserForm.getId());
            //kakaoUserForm.setNickname(nickname);


            /**Member user = memberOpt.get(); // 지금 접근한 사용자 Member 객체 가져오기
            member.setNickname(nickname);
            memberRepository.save(member);**/


            kakaoUserForm.setNickname(nickname);
            memberService.signUp(kakaoUserForm);

            //카카오 아이디, 카카오 닉네임, 이길로 닉네임으로 jwt 토큰 생성
            //JwtDto kakaoJwtToken = jwtIssuer.createToken(kakaoUserForm.getId(), kakaoUserForm.getName(), kakaoUserForm.getNickname());
            //kakaoUserForm.setRefresh_token(kakaoJwtToken.getRefreshToken());

            //String userId = JwtUtil.extractUserId(token);

            log.info("닉네임 저장");
            //log.info("access token: {}, refresh token: {}", kakaoJwtToken.getAccessToken(), kakaoJwtToken.getRefreshToken());
            //return ResponseEntity.ok(kakaoJwtToken);
            return ResponseEntity.ok("닉네임 저장");
        }
    }

}