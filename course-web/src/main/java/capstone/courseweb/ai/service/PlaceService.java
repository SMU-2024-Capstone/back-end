package capstone.courseweb.ai.service;

import capstone.courseweb.ai.*;
//import capstone.courseweb.rating.RatingService;
//import capstone.courseweb.ai.rating.Rating;
//import capstone.courseweb.ai.rating.RatingRepository;
import capstone.courseweb.rating.Rating;
import capstone.courseweb.rating.RatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final RatingRepository ratingRepository;


    public List<PlaceResponse> getPlacesByCategoryAndPage(String category, String userid) {

        List<Place> places;
        log.info("전달된 카테고리 출력: {}", category);

        if ("전체".equals(category)) {
            // 카테고리가 "전체"일 때 모든 장소 조회
            places = placeRepository.findAll();
        } else {
            // 카테고리가 특정 값일 때 해당 카테고리의 장소 조회
            places = placeRepository.findByCategory(category);
        }

        log.info("db에서 가져온 places 리스트 출력: {}", places.get(0));

        return places.stream()
                .map(place -> {
                    int rating = getRatingForPlace(place.getId(), userid);
                    return new PlaceResponse(place.getTag(), place.getName(), place.getCategory(), rating);
                })
                .collect(Collectors.toList());
    }

    public int getRatingForPlace(int placeId, String userId) {

        log.info("getRatingForPlace에서 받은 placeId 출력 출력: {}", placeId);
        log.info("getRatingForPlace에서 받은 userId 출력 출력: {}", userId);
        // 특정 장소에 대한 평점 조회
        Optional<Rating> optionalRating = ratingRepository.findByPlaceIDAndUserID(placeId, userId);

        // 평점이 없으면 0 반환
        return optionalRating.map(Rating::getRating).orElse(0);
    }
}
