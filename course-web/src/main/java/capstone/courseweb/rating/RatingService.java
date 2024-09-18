package capstone.courseweb.rating;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class RatingService {
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
