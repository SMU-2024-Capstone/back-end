package capstone.courseweb.ai;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class PreferenceService {

    public String sendResultToFlaskServer(Map<String, Object> testResult) {
        // RestTemplate 사용해 Flask 서버로 POST 요청
        RestTemplate restTemplate = new RestTemplate();
        String flaskUrl = "http://127.0.0.1:5000/test-result/calc";  // Flask 서버 주소

        //Flask 서버로 보낼 요청 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(testResult, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(flaskUrl, request, String.class);

        return response.getBody();
    }

    public String sendUserVectorToFlaskServer(Map<String, Object> userVectorMap) {
        // RestTemplate 사용해 Flask 서버로 POST 요청
        RestTemplate restTemplate = new RestTemplate();
        String flaskUrl = "http://127.0.0.1:5000/login/ai";  // Flask 서버 주소

        //Flask 서버로 보낼 요청 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(userVectorMap, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(flaskUrl, request, String.class);

        return response.getBody();

    }
}
