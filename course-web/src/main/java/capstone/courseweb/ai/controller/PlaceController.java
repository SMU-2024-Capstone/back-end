package capstone.courseweb.ai.controller;

import capstone.courseweb.ai.PlaceResponse;
import capstone.courseweb.ai.service.PlaceService;
import capstone.courseweb.jwt.service.AuthService;
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
    private final AuthService authService;

    @GetMapping("/places")
    public ResponseEntity<?> getPlaces(
            @RequestParam("category") String category) {

        log.info("/places에 요청 들어옴");
        Member member = authService.getAuthenticatedMember();
        String id = member.getId();
        log.info("닉네임 방식으로 했을 때 id: {}", id);
        log.info("입력한 카테고리 출력: {}", category);
        log.info("아이디 출력: {}", id);
        List<PlaceResponse> places = placeService.getPlacesByCategoryAndPage(category, id);
        log.info("places: {}", places.get(0));
        log.info("총 개수: {}", places.size());
        return ResponseEntity.ok(places);
    }

}