package capstone.courseweb.user.service;

import capstone.courseweb.user.domain.SignUpForm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 인가코드로 access token 발급
 * 발급받은 access token으로 kakao에 사용자 정보 받아오기
 */
@Slf4j
@Service
@RequiredArgsConstructor

public class UserService {

    public SignUpForm getUserInfo(String code) throws JsonProcessingException{

        String accessToken = getAccessToken(code);
        log.info("access token: {}",accessToken);

        String kakaoUserInfo = getKakaoUserInfo(accessToken);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(kakaoUserInfo);
        //id, nickname 추출
        String id = jsonNode.get("id").asText();
        String nickname = jsonNode.get("properties").get("nickname").asText();
        log.info("id: {}, nickname: {}", id, nickname);

        //추출한 id, nickname과 provider로 signupform 생성
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setId(id);
        signUpForm.setName(nickname);
        signUpForm.setProvider("KAKAO");

        //memberService.signUp(signUpForm); //db에 저장

        return signUpForm;
    }

    // access token 발급
    public String getAccessToken(String code) {
        log.info("getAccessToken");
        RestTemplate rt = new RestTemplate();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "9d8918ecc1284404c0cfcc0d03046079");
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

        /*
        Long id = jsonNode.get("id").asLong();
        String email = jsonNode.get("kakao_account").get("email").asText();
        String nickname = jsonNode.get("properties").get("nickname").asText();

        userInfo.put("id",id);
        userInfo.put("email",email);
        userInfo.put("nickname",nickname);

        return userInfo;

         */
        return response.getBody();
    }

}
