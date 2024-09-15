package capstone.courseweb.rating;

import capstone.courseweb.ai.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Integer> { // ID 타입을 Integer로 수정
    Optional<Rating> findById(Integer ratingID);
}