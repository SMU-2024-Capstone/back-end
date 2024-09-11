package capstone.courseweb.ai;

import jakarta.persistence.*;

@Entity
@Table(name = "PLACE")
public class AiPlaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer naver_id;

    private String name;

    private String category;
    private String tag;
    private String address;

}