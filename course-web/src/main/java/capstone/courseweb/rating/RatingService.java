package capstone.courseweb.rating;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class RatingService {
    /*
{
    "user_vector": " ",
    "total_rating": 20,   // 여지껏 매긴 별점 합
    "count_rating_places": 6,  // 여지껏 별점 매긴 장소 수

    "placeID": 1336760258,  // 현재 별점 매긴 장소
    "rating": 5  // 현재 매긴 별점
 }
     */

    public String sendRatingToFlaskServer(Map<String, Object> ratingRequest) {
        // RestTemplate 사용해 Flask 서버로 POST 요청
        RestTemplate restTemplate = new RestTemplate();
        String flaskUrl = "http://127.0.0.1:5000/rating";  // Flask 서버 주소

        //Flask 서버로 보낼 요청 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(ratingRequest, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(flaskUrl, request, String.class);

        return response.getBody();
    }
}
