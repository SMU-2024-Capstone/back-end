package capstone.courseweb.rating;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString
public class Rating {
    @Id
    private String ratingID;    // userID+placeID
    private Integer placeID;
    private String userID;
    private Integer rating;
}
