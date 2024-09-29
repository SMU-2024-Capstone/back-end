package capstone.courseweb.ai;

import capstone.courseweb.ai.service.PlaceService;
import capstone.courseweb.jwt.service.AuthService;
import capstone.courseweb.rating.RatingRepository;
import capstone.courseweb.user.domain.Member;
import capstone.courseweb.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PreferTestController {

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
            ratingRepository.findByUserID(user.getId()).get();
            userVector = user.getUser_vector();
        }
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
            }
        }
        finalResponse.put("ai_recommend", places_info);

        return ResponseEntity.ok(finalResponse);

    }


    @PostMapping("/home/ai")
    public ResponseEntity<Map<String, List<Object>>> sendAiPlaces() {

        Member user = authService.getAuthenticatedMember();
        String id = user.getId();

        String userVector = user.getUser_vector();

        Map<String, Object> userVectorMap = new HashMap<>();
        userVectorMap.put("user_vector", userVector);

        String flaskResponse = preferenceService.sendUserVectorToFlaskServer(userVectorMap);
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
            }
        }
        finalResponse.put("ai_recommend", places_info);

        return ResponseEntity.ok(finalResponse);

    }

}
