package capstone.courseweb.rating;

import capstone.courseweb.ai.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, String> { // ID 타입을 Integer로 수정
    Optional<Rating> findByRatingID(String ratingID);

    Optional<List<Rating>> findByUserID(String userID);

    Optional<Rating> findByPlaceIDAndUserID(int placeID, String userID); // 필드 이름을 'Placeid'와 'Userid'로 수정

    @Query(value =
            "SELECT "+
                    "userID, "+
                    "SUM(rating) AS totalRating, " +
                    "COUNT(rating) AS countRatingPlaces, " +
                    "FROM RATING " +
                    "WHERE userID = :userID "+
                    "GROUP BY userID",
            nativeQuery = true)
    List<RatingSumInterface> findGroupByRating(@Param("userID") String userID);
}