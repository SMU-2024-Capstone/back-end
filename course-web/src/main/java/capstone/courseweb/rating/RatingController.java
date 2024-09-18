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

@Slf4j
@RestController
@RequiredArgsConstructor
public class RatingController {

    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;
    private final RatingRepository ratingRepository;
    private final RatingService ratingService;

    @PostMapping("/rating")
    public ResponseEntity<Map<String, List<Object>>> receiveRating(@RequestBody Map<String, Object> ratingMap) {
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

        /* test -> 통과 됨

        if (memberRepository.findByNickname("현조").isEmpty()){
            Map<String, List<Object>> errorResponse = new HashMap<>();
            errorResponse.put("error", Collections.singletonList("해당되는 유저가 없습니다."));
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(errorResponse);
        }
        Member member = memberRepository.findByNickname("현조").get();
        String userID = member.getId();
        log.info("userID: {}", userID);


         */


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
        log.info("user_vector: {}", userVector);

        // placeID
        Integer placeID = placeRepository.findByName(placename).get().getId();
        ratingRequest.put("placeID", placeID);
        log.info("placeID: {}", placeID);

        // rating
        Integer rating = (Integer) ratingMap.get("rating");
        ratingRequest.put("rating", rating);
        log.info("rating: {}", rating);

        String ratingID = userID + '-' + placeID.toString();
        log.info("ratingID: {}", ratingID);



        // 만약 이미 별점을 매긴 장소의 별점을 수정한 경우일 때 -> 기존 별점 삭제
        if(ratingRepository.findByRatingID(ratingID).isPresent()){
            log.info("별점 수정하는 경우");
            ratingRepository.deleteById(ratingID);
        }

        // 별점 매기는 게 아예 처음인 경우 (total_rating, count_rating_places 이 0)
        if (ratingRepository.findGroupByRating(userID).isEmpty()) {
            log.info("별점 매기는 게 아예 처음인 경우");
            ratingRequest.put("total_rating", 0);
            ratingRequest.put("count_rating_places", 0);
        }
        else {
            Integer totalRating = ratingRepository.findGroupByRating(userID).get(0).getTotalRating();
            Integer countRatingPlaces = ratingRepository.findGroupByRating(userID).get(0).getCountRatingPlaces();
            ratingRequest.put("total_rating", totalRating);
            ratingRequest.put("count_rating_places", countRatingPlaces);
            log.info("total_rating: {}", totalRating);
            log.info("count_rating_places: {}", countRatingPlaces);
        }



        log.info("ratingRequest: {}", ratingRequest);
        String flaskResponse = ratingService.sendRatingToFlaskServer(ratingRequest);
        JSONObject flaskResponseJson = new JSONObject(flaskResponse);

        // user vector 저장
        String updatedUserVector = flaskResponseJson.get("user_vector").toString();
        log.info("updatedUserVector: {}", updatedUserVector);
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
        log.info("save rating");

        Map<String, List<Object>> finalResponse = new HashMap<>();

        return ResponseEntity.ok(finalResponse);
    }
}
