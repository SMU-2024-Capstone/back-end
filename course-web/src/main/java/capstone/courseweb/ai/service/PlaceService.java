package capstone.courseweb.ai.service;

import capstone.courseweb.ai.*;
//import capstone.courseweb.rating.RatingService;
import capstone.courseweb.ai.rating.Rating;
import capstone.courseweb.ai.rating.RatingRepository;
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
    //private final RatingService ratingService;
    private final RatingRepository ratingRepository;


    public List<PlaceResponse> getPlacesByCategoryAndPage(String category, int pageNumber, String userid) {
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        List<Place> places;// = placeRepository.findByCategory(category, pageable);

        if ("전체".equals(category)) {
            // 카테고리가 "전체"일 때 모든 장소 조회
            places = placeRepository.findAll(pageable).getContent();
        } else {
            // 카테고리가 특정 값일 때 해당 카테고리의 장소 조회
            places = placeRepository.findByCategory(category, pageable);
        }

        return places.stream()
                .map(place -> {
                    int rating = getRatingForPlace(place.getId(), userid);
                    return new PlaceResponse(place.getTag(), place.getName(), place.getCategory(), rating);
                })
                .collect(Collectors.toList());
    }

    public int getRatingForPlace(int placeId, String userId) {
        // 특정 장소에 대한 평점 조회
        Optional<Rating> optionalRating = ratingRepository.findByPlaceidAndUserid(placeId, userId);

        // 평점이 없으면 0 반환
        return optionalRating.map(Rating::getRating).orElse(0);
    }
}
