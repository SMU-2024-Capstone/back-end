package capstone.courseweb.random.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteDto {
    //private String startPlace; //시작 장소 이름
    //private String endPlace; //도착 장소 이름
    //private String sx; //출발지 경도
    //private String sy; //출발지 위도
    //private String ex; //도착지 경도
    //private String ey; //도착지 위도
    private String routeDescription; // 경로 설명
    private String distance; // 거리
    private String duration; // 소요 시간
    //private String startPlaceURL;
    //private String endPlaceURL;
}
