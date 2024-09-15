package capstone.courseweb.ai;

import jakarta.persistence.Entity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

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
