package capstone.courseweb.ai;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AIPlaceDto {
    private Integer naver_id;
    private String name;
    private String category;
    private String tag;
    private String address;

    public Place toEntity() {
        return Place.builder()
                .naver_id(this.naver_id)
                .name(this.name)
                .category(this.category)
                .tag(this.tag)
                .address(this.address)
                .build();
    }
}
