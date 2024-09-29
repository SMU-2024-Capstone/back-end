package capstone.courseweb.ai;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceResponse {
        private String tag;
        private String placename;
        private String category;
        private int rating;
}
