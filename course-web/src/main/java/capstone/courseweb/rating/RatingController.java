package capstone.courseweb.rating;

import capstone.courseweb.ai.PlaceRepository;
import capstone.courseweb.jwt.service.AuthService;
import capstone.courseweb.user.domain.Member;
import capstone.courseweb.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RatingController {

    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;
    private final RatingRepository ratingRepository;
    private final RatingService ratingService;
    private final AuthService authService;

    @PostMapping("/rating")
    public ResponseEntity<Map<String, List<Object>>> receiveRating(@RequestBody Map<String, Object> ratingMap) {

        Member member = authService.getAuthenticatedMember();
        String userID = member.getId();

        String placename = ratingMap.get("placename").toString();
        if(placeRepository.findByName(placename).isEmpty()) {
            Map<String, List<Object>> errorResponse = new HashMap<>();
            errorResponse.put("error", Collections.singletonList("placename에 해당되는 장소가 없습니다"));
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(errorResponse);
        }

        // flask request body
        Map<String, Object> ratingRequest = new HashMap<>();

        // user_vector
        String userVector = member.getUser_vector();
        ratingRequest.put("user_vector", userVector);

        // placeID
        Integer placeID = placeRepository.findByName(placename).get().getId();
        ratingRequest.put("placeID", placeID);

        // rating
        Integer rating = (Integer) ratingMap.get("rating");
        ratingRequest.put("rating", rating);

        String ratingID = userID + '-' + placeID.toString();



        // 만약 이미 별점을 매긴 장소의 별점을 수정한 경우일 때 -> 기존 별점 삭제
        if(ratingRepository.findByRatingID(ratingID).isPresent()){
            ratingRepository.deleteById(ratingID);
        }

        // 별점 매기는 게 아예 처음인 경우 (total_rating, count_rating_places 이 0)
        if (ratingRepository.findGroupByRating(userID).isEmpty()) {
            ratingRequest.put("total_rating", 0);
            ratingRequest.put("count_rating_places", 0);
        }
        else {
            Integer totalRating = ratingRepository.findGroupByRating(userID).get(0).getTotalRating();
            Integer countRatingPlaces = ratingRepository.findGroupByRating(userID).get(0).getCountRatingPlaces();
            ratingRequest.put("total_rating", totalRating);
            ratingRequest.put("count_rating_places", countRatingPlaces);
        }


        String flaskResponse = ratingService.sendRatingToFlaskServer(ratingRequest);
        JSONObject flaskResponseJson = new JSONObject(flaskResponse);

        // user vector 저장
        String updatedUserVector = flaskResponseJson.get("user_vector").toString();
        member.setUser_vector(updatedUserVector);
        memberRepository.save(member);

        // 새로 매긴 별점 저장
        ratingRepository.save(
                Rating.builder()
                        .ratingID(ratingID)
                        .userID(userID)
                        .placeID(placeID)
                        .rating(rating)
                        .build()
        );

        Map<String, List<Object>> finalResponse = new HashMap<>();

        return ResponseEntity.ok(finalResponse);
    }
}
