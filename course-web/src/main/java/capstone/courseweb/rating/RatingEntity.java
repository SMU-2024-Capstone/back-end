package capstone.courseweb.rating;

import capstone.courseweb.place.PlaceEntity;
import capstone.courseweb.user.domain.Member;
import jakarta.persistence.*;

@Entity
@Table(name = "ratings")
public class RatingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Member user;

    @ManyToOne
    @JoinColumn(name = "place_id", nullable = false)
    private PlaceEntity place;

    private Integer rating;

}
