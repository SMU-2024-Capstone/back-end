package capstone.courseweb.ai;

import capstone.courseweb.ai.service.PlaceService;
import capstone.courseweb.jwt.config.JwtAuthProvider;
import capstone.courseweb.jwt.service.AuthService;
import capstone.courseweb.jwt.utility.JwtIssuer;
import capstone.courseweb.rating.RatingRepository;
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
    private final RatingRepository ratingRepository;
    private final AuthService authService;

    @PostMapping("/test-result")
    public ResponseEntity<Map<String, List<Object>>> receiveTestResult(@RequestBody Map<String, Object> testResult) {

        Member user = authService.getAuthenticatedMember();

        String userVector = "0";
        // 사용자가 기존에 저장한 별점이 있다면 -> 기존 userVector를 전달해줌
        if (ratingRepository.findByUserID(user.getId()).isPresent() && ratingRepository.findByUserID(user.getId()).get().size()>0){
            log.info("저장한 별점이 있음: {}", ratingRepository.findByUserID(user.getId()).get());
            ratingRepository.findByUserID(user.getId()).get();
            userVector = user.getUser_vector();
        }
        log.info("user_vector: {}", userVector);
        testResult.put("user_vector", userVector);
        String flaskResponse = preferenceService.sendResultToFlaskServer(testResult);
        JSONObject flaskResponseJson = new JSONObject(flaskResponse);

        String updatedUserVector = flaskResponseJson.get("user_vector").toString();

        //유저 벡터 저장

        user.setUser_vector(updatedUserVector);
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

        Member user = authService.getAuthenticatedMember();
        String id = user.getId();
        log.info("jwt 토큰 검증 받은 사용자 id: {}", id);

        String nickname = user.getNickname();
        log.info("jwt 토큰 검증 받은 사용자 nickname: {}", nickname);

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
