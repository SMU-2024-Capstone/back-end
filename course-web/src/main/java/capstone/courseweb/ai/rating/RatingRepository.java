package capstone.courseweb.ai.rating;

import capstone.courseweb.ai.rating.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> { // ID 타입을 Integer로 수정
    Optional<Rating> findById(Integer id); // ID 타입을 Integer로 수정

    Optional<List<Rating>> findByUserid(String userid); // 필드 이름을 'Userid'로 수정

    Optional<Rating> findByPlaceidAndUserid(int placeid, String userid); // 필드 이름을 'Placeid'와 'Userid'로 수정
}

