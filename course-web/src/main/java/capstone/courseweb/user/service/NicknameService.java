package capstone.courseweb.user.service;

import capstone.courseweb.user.domain.Member;
import capstone.courseweb.user.repository.MemberRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NicknameService {

    private final MemberRepository memberRepository;

    public Map<String, Object> checkAndSetNickname(String nickname, String userid) {
        if (memberRepository.existsByNickname(nickname)) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.OK.value());
            response.put("message", "닉네임 중복");
            return response;
        } else { //닉네임 중복 아닐 때
            //닉네임 저장

            Optional<Member> memberOpt = memberRepository.findById(userid);
            Member user = memberOpt.get();
            log.info(user.getId());
            log.info(user.getName());
            //Member user = memberRepository.findById(userid)
              //      .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

            // 닉네임 업데이트
            user.setNickname(nickname);
            memberRepository.save(user); // 변경된 정보 저장
            log.info("유저 컨트롤러 nickname 출력: {}", nickname);

            Map<String, Object> response = new HashMap<>();
            response.put("status", HttpStatus.OK.value());
            response.put("message", "닉네임 사용 가능");
            return response;
        }
    }
}
