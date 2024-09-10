package capstone.courseweb.ai;

import capstone.courseweb.jwt.config.JwtAuthProvider;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PreferTestController {

    private final JwtAuthProvider jwtAuthProvider;
    private final PreferenceService preferenceService;

    @PostMapping("/test-result")
    public ResponseEntity<String> receiveTestResult(@RequestBody Map<String, Object> testResult) { //, @RequestHeader("Authorization")String token

        System.out.println(testResult.get("result"));


        //jwt 토큰 검증
        /**if (!jwtAuthProvider.validateToken(token.substring(7))) { //Bearer<토큰값>으로 전송되기 때문에 7번째 위치부터(토큰값만 추출)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token"); //HTTP 401 Unauthorized 상태 코드를 반환
        }**/


        //jwt 검증되면 flask 서버로 데이터 전달
        String response = preferenceService.sendToFlaskServer(testResult);
        System.out.println("플라스크에 전송");
        System.out.println(response);


        /** 플라스크에서 받아온 정보 중에서 유저벡터는 디비저장, 장소들은 프론트로 전송 **/

        //return ResponseEntity.ok("ddsfdf");
        return ResponseEntity.ok(response); //flask에서 받은 데이터 프론트로 전달
    }
}
