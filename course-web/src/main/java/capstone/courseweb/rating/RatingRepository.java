package capstone.courseweb.rating;

public interface RatingRepository {
    RatingEntity findByUserIdAndPlaceId(String userId, Long placeId);
}
