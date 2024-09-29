package capstone.courseweb.user.service;

import capstone.courseweb.jwt.JwtDto;
import capstone.courseweb.jwt.utility.JwtIssuer;
import capstone.courseweb.user.domain.Member;
import capstone.courseweb.user.domain.SignUpForm;
import capstone.courseweb.user.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 인가코드로 access token 발급
 * 발급받은 access token으로 kakao에 사용자 정보 받아오기
 */
@Slf4j
@Service
@RequiredArgsConstructor

public class UserService {

    @Value("${kakao.api-key}")
    private String CLIENT_ID;
    private SignUpForm kakaoUserForm;
    private final MemberRepository memberRepository;
    private final JwtIssuer jwtIssuer;
    private final MemberService memberService;

    public Map<String, Object> handleKakaoLogin(String code) throws JsonProcessingException {

        kakaoUserForm = getUserInfo(code);
        Optional<Member> memberOpt = memberRepository.findById(kakaoUserForm.getId());

        if (memberOpt.isPresent()) { //db에 회원정보 있을 때
            Member user = memberOpt.get();
            if (user.getNickname()==null) { // 닉네임 없으면 닉네임 화면으로
                Map<String, Object> response = new HashMap<>();
                response.put("status", HttpStatus.OK.value());
                response.put("message", "닉네임 없음");
                return response;
            } else { //닉네임 있으면 유저벡터 있는지 확인
                if (user.getUser_vector()==null) { //유저벡터 없으면 선호도 테스트 화면으로
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", HttpStatus.OK.value());
                    response.put("nickname", user.getNickname());
                    response.put("message", "선호도 테스트");
                    return response;
                } else { //유저벡터까지 있으면 회원가입 완료 -> 홈화면
                    JwtDto kakaoJwtToken = jwtIssuer.createToken(kakaoUserForm.getId(), kakaoUserForm.getName());
                    kakaoUserForm.setRefresh_token(kakaoJwtToken.getRefreshToken());
                    memberRepository.save(user);

                    Member member = memberOpt.get();
                    member.setRefresh_token(kakaoJwtToken.getRefreshToken());
                    memberRepository.save(member);
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", HttpStatus.OK.value());
                    response.put("nickname", user.getNickname());
                    response.put("message", "홈화면");
                    response.put("token", kakaoJwtToken);

                    return response;
                }
            }

        }
        else { //db에 회원정보 없을 때
            JwtDto kakaoJwtToken = jwtIssuer.createToken(kakaoUserForm.getId(), kakaoUserForm.getName());
            kakaoUserForm.setRefresh_token(kakaoJwtToken.getRefreshToken());
            memberService.signUp(kakaoUserForm);

            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.CREATED.value());
            response.put("token", kakaoJwtToken);
            return response;
        }

    }

    public SignUpForm getUserInfo(String code) throws JsonProcessingException{

        String accessToken = getAccessToken(code);
        String kakaoUserInfo = getKakaoUserInfo(accessToken);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(kakaoUserInfo);
        String id = jsonNode.get("id").asText();
        String nickname = jsonNode.get("properties").get("nickname").asText();

        //추출한 id, nickname과 provider로 signupform 생성
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setId(id);
        signUpForm.setName(nickname);


        return signUpForm;
    }

    // access token 발급
    public String getAccessToken(String code) {

        RestTemplate rt = new RestTemplate();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", CLIENT_ID);
        params.add("redirect_uri", "http://localhost:5173/login/oauth2/code/kakao");
        params.add("code", code);


        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        ResponseEntity<String> accessTokenResponse = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = accessTokenResponse.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        assert jsonNode != null;
        return jsonNode.get("access_token").asText(); //토큰 전송
    }


    // 토큰으로 카카오 API 호출
    private String getKakaoUserInfo(String accessToken) {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        return response.getBody();
    }

}
