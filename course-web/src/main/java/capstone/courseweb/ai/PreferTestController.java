package capstone.courseweb.ai;

import capstone.courseweb.ai.service.PlaceService;
import capstone.courseweb.jwt.config.JwtAuthProvider;
import capstone.courseweb.jwt.utility.JwtIssuer;
import capstone.courseweb.user.domain.Member;
import capstone.courseweb.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PreferTestController {

    private final JwtAuthProvider jwtAuthProvider;
    private final JwtIssuer jwtIssuer;
    private final PreferenceService preferenceService;
    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;
    private final PlaceService placeService;

    @PostMapping("/test-result")
    public ResponseEntity<Map<String, List<Object>>> receiveTestResult(@RequestBody Map<String, Object> testResult) { //, @RequestHeader("Authorization")String token

        //jwt 토큰 검증
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, List<Object>> errorResponse = new HashMap<>();
            errorResponse.put("error", Collections.singletonList("Invalid JWT token"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        String nickname = authentication.getName(); // 사용자의 id 가져오기 (JwtAuthProvider에서 사용자 ID를 subject로 저장한 경우)
        log.info("jwt 토큰 검증 받은 사용자 id: {}",  nickname);


        // String nickname = "현조"; // test 완료
        Optional<Member> memberOpt = memberRepository.findByNickname(nickname);
        if (memberOpt.isEmpty()) {
            Map<String, List<Object>> errorResponse = new HashMap<>();
            errorResponse.put("error", Collections.singletonList("User not found"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        System.out.println("플라스크에 전송");

        String flaskResponse = preferenceService.sendResultToFlaskServer(testResult);
        JSONObject flaskResponseJson = new JSONObject(flaskResponse);

        String userVector = flaskResponseJson.get("user_vector").toString();

        //유저 벡터 저장
        Member user = memberOpt.get();
        user.setUser_vector(userVector);
        memberRepository.save(user);



        JSONArray jsonArray = new JSONArray(flaskResponseJson.getJSONArray("placeID"));
        int[] intArray = new int[jsonArray.length()];

        // JSONArray 값들을 int로 변환하여 배열에 저장
        for (int i = 0; i < jsonArray.length(); i++) {
            intArray[i] = jsonArray.getInt(i);
        }

        Map<String, List<Object>> finalResponse = new HashMap<>();
        List<Object> places_info = new ArrayList<>();

        for (int i = 0; i < intArray.length; i++){
            Optional<Place> place = placeRepository.findById(intArray[i]);

            if (place.isPresent()) {
                int rating = placeService.getRatingForPlace(place.get().getId(), user.getId());

                Map<String, Object> newResponse = new HashMap<>();
                newResponse.put("placename", place.get().getName());
                newResponse.put("category", place.get().getCategory());
                newResponse.put("tag", place.get().getTag());
                newResponse.put("URL", "https://pcmap.place.naver.com/restaurant/"+place.get().getId());
                newResponse.put("rating", rating);
                places_info.add(newResponse);
                System.out.println(newResponse);
            }
        }
        finalResponse.put("ai_recommend", places_info);

        return ResponseEntity.ok(finalResponse);

    }


    @PostMapping("/home/ai")
    public ResponseEntity<Map<String, List<Object>>> sendAiPlaces() { //선호도 테스트 다시 할 때는 test/result로 받아야 함.


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, List<Object>> errorResponse = new HashMap<>();
            errorResponse.put("error", Collections.singletonList("Invalid JWT token"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }


        Member member = (Member) authentication.getPrincipal();
        String id = member.getId();
        log.info("jwt 토큰 검증 받은 사용자 id: {}", id);

        String nickname = member.getNickname();
        log.info("jwt 토큰 검증 받은 사용자 nickname: {}", nickname);


        // String id = "현조";  // test 완료

        Optional<Member> memberOpt = memberRepository.findById(id);
        if (memberOpt.isEmpty()) {
            Map<String, List<Object>> errorResponse = new HashMap<>();
            errorResponse.put("error", Collections.singletonList("User not found"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        Member user = memberOpt.get();
        String userVector = user.getUser_vector();
        log.info("uservector 확인: {}", userVector);

        Map<String, Object> userVectorMap = new HashMap<>();
        userVectorMap.put("user_vector", userVector);

        String flaskResponse = preferenceService.sendUserVectorToFlaskServer(userVectorMap);
        log.info("flaskR {}", flaskResponse);
        JSONObject flaskResponseJson = new JSONObject(flaskResponse);


        JSONArray jsonArray = new JSONArray(flaskResponseJson.getJSONArray("placeID"));
        int[] intArray = new int[jsonArray.length()];

        // JSONArray 값들을 int로 변환하여 배열에 저장
        for (int i = 0; i < jsonArray.length(); i++) {
            intArray[i] = jsonArray.getInt(i);
        }

        Map<String, List<Object>> finalResponse = new HashMap<>();
        List<Object> places_info = new ArrayList<>();

        for (int i = 0; i < intArray.length; i++){
            Optional<Place> place = placeRepository.findById(intArray[i]);

            if (place.isPresent()) {
                int rating = placeService.getRatingForPlace(place.get().getId(), id);
                Map<String, Object> newResponse = new HashMap<>();
                newResponse.put("placename", place.get().getName());
                newResponse.put("category", place.get().getCategory());
                newResponse.put("tag", place.get().getTag());
                newResponse.put("URL", "https://pcmap.place.naver.com/restaurant/"+place.get().getId());
                newResponse.put("rating", rating);
                places_info.add(newResponse);
                System.out.println(newResponse);
            }
        }
        finalResponse.put("ai_recommend", places_info);

        return ResponseEntity.ok(finalResponse);

    }

}
