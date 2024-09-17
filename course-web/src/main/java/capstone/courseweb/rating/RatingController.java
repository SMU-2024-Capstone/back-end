package capstone.courseweb.rating;

import capstone.courseweb.ai.Place;
import capstone.courseweb.ai.PlaceRepository;
import capstone.courseweb.user.domain.Member;
import capstone.courseweb.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

// TODO: 완성 x, 지금 하는 중
@Slf4j
@RestController
@RequiredArgsConstructor
public class RatingController {

    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;
    private final RatingRepository ratingRepository;

    /*
    {
    "placename": "함께식탁",
    "rating": 5
    }

    {
    "user_vector": "",
    "total_rating": 20,   // 여지껏 매긴 별점 합
    "count_rating_places": 6,  // 여지껏 별점 매긴 장소 수

    "placeID": 1336760258,  // 현재 별점 매긴 장소
    "rating": 5  // 현재 매긴 별점
    }
     */
    @PostMapping("/rating")
    public ResponseEntity<Map<String, List<Object>>> receiveRating(@RequestBody Map<String, Object> ratingMap) { //선호도 테스트 다시 할 때는 test/result로 받아야 함.

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, List<Object>> errorResponse = new HashMap<>();
            errorResponse.put("error", Collections.singletonList("Invalid JWT token"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }


        Member member = (Member) authentication.getPrincipal();
        String userID = member.getId();
        log.info("jwt 토큰 검증 받은 사용자 id: {}", userID);

        String nickname = member.getNickname();
        log.info("jwt 토큰 검증 받은 사용자 nickname: {}", nickname);



        String placename = ratingMap.get("placename").toString();
        if(placeRepository.findByName(placename).isEmpty()) {
            Map<String, List<Object>> errorResponse = new HashMap<>();
            errorResponse.put("error", Collections.singletonList("placename에 해당되는 장소가 없습니다"));
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(errorResponse);
        }

        Integer placeID = placeRepository.findByName(placename).get().getId();
        String ratingID = userID + '-' + placeID.toString();
        // 만약 이미 별점을 매긴 장소의 별점을 수정한 경우일 때
        if(ratingRepository.findByRatingID(ratingID).isPresent()){

        }
        // 아닐 경우
        else {
            // 별점을 처음 매기는 경우
            if(ratingRepository.findByUserID(userID).isEmpty()) {

            }
            // 처음이 아닌 경우
            else {
                List<Rating> ratingList = ratingRepository.findByUserID(userID).get();

            }
        }






        Integer rating = (Integer) ratingMap.get("rating");



        Map<String, List<Object>> finalResponse = new HashMap<>();


        return ResponseEntity.ok(finalResponse);

    }
}
