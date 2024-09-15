package capstone.courseweb.ai.controller;

import capstone.courseweb.ai.PlaceResponse;
import capstone.courseweb.ai.service.PlaceService;
import capstone.courseweb.user.domain.Member;
import capstone.courseweb.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;
    private final MemberRepository memberRepository;

    @GetMapping("/places")
    public ResponseEntity<?> getPlaces( //List<PlaceResponse>
            @RequestParam String category,
            @RequestParam int pageNumber) {

        log.info("/places에 요청 들어옴");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, List<Object>> errorResponse = new HashMap<>();
            errorResponse.put("error", Collections.singletonList("Invalid JWT token"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        /**닉네임에서는 이렇게하고 **/
        Member member = (Member) authentication.getPrincipal();
        String id = member.getId();
        log.info("닉네임 방식으로 했을 때 id: {}", id);

        /**프리퍼런스에서는 이렇게 함.. 둘다되긴하는듯?? **/
        String nickname = authentication.getName(); // 사용자의 id 가져오기 (JwtAuthProvider에서 사용자 ID를 subject로 저장한 경우)
        log.info("프리프런스 방식으로 했을 때 nickname: {}",  nickname);

        //저장할 거 아니면 안써도 될 듯
        /*Optional<Member> memberOpt = memberRepository.findById(id);
        if (memberOpt.isEmpty()) {
            Map<String, List<Object>> errorResponse = new HashMap<>();
            errorResponse.put("error", Collections.singletonList("User not found"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }*/


        List<PlaceResponse> places = placeService.getPlacesByCategoryAndPage(category, pageNumber, id);
        log.info("places: {}", places.get(0));
        return ResponseEntity.ok(places);
    }

}