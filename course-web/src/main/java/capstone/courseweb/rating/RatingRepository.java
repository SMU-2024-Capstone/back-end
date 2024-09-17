package capstone.courseweb.rating;

import capstone.courseweb.ai.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Integer> { // ID 타입을 Integer로 수정
    Optional<Rating> findByRatingID(String ratingID);

    Optional<List<Rating>> findByUserID(String userID);

    Optional<Rating> findByPlaceIDAndUserID(int placeID, String userID); // 필드 이름을 'Placeid'와 'Userid'로 수정

}