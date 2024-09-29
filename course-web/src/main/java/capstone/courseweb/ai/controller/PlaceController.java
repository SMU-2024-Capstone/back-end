package capstone.courseweb.ai.controller;

import capstone.courseweb.ai.PlaceResponse;
import capstone.courseweb.ai.service.PlaceService;
import capstone.courseweb.jwt.service.AuthService;
import capstone.courseweb.user.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

        Member member = authService.getAuthenticatedMember();
        String id = member.getId();
        List<PlaceResponse> places = placeService.getPlacesByCategoryAndPage(category, id);

        Map<String, Object> response = new HashMap<>();
        response.put("rating_places", places);

        return ResponseEntity.ok(response);
    }

}