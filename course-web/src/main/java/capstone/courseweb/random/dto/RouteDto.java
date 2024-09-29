package capstone.courseweb.random.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteDto {
    private String routeDescription; // 경로 설명
    private String distance; // 거리
    private String duration; // 소요 시간
}
