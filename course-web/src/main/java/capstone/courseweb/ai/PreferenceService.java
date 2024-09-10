package capstone.courseweb.ai;

import capstone.courseweb.user.domain.Member;
import capstone.courseweb.user.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
public class PreferenceService {

    public String sendToFlaskServer(Map<String, Object> testResult) {
        // RestTemplate 사용해 Flask 서버로 POST 요청
        RestTemplate restTemplate = new RestTemplate();
        String flaskUrl = "http://localhost:5000/api/process";  // Flask 서버 주소

        //Flask 서버로 보낼 요청 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(testResult, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(flaskUrl, request, String.class);
        return response.getBody();
    }

    /*@Autowired
    private PreferenceRepository preferenceRepository;

    @Autowired
    private MemberRepository memberRepository;

    public void savePreferences(PreferenceDto preferenceDto, String userId) {
        //userid로 객체 찾기
        Optional<Member> memberOpt = memberRepository.findById(userId);

        if(memberOpt.isPresent()) {
            Member member = memberOpt.get();

            PreferenceEntity preferenceEntity = new PreferenceEntity();
            preferenceEntity.setUser(member);

            //preferenceEntity.setAtmosphereCategory1(preferenceDto.getAtmosphereCategory1());
            //preferenceEntity.setAtmosphereCategory2(preferenceDto.getAtmosphereCategory2());
            //preferenceEntity.setAtmosphereCategory3(preferenceDto.getAtmosphereCategory3());
            preferenceEntity.setImportanceCategory1(preferenceDto.getImportanceCategory1());
            preferenceEntity.setImportanceCategory2(preferenceDto.getImportanceCategory2());
            preferenceEntity.setImportanceCategory3(preferenceDto.getImportanceCategory3());
            preferenceEntity.setImportanceCategory4(preferenceDto.getImportanceCategory4());
            preferenceEntity.setImportanceCategory5(preferenceDto.getImportanceCategory5());

            preferenceRepository.save(preferenceEntity);
        } else {
            throw new IllegalArgumentException("해당하는 사용자를 찾을 수 없습니다.");
        }
    }*/
}
