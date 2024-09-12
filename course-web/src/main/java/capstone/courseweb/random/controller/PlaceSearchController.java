package capstone.courseweb.random.controller;

import capstone.courseweb.ai.PreferenceService;
import capstone.courseweb.jwt.config.JwtAuthProvider;
import capstone.courseweb.jwt.utility.JwtIssuer;
import capstone.courseweb.random.domain.SearchForm;
import capstone.courseweb.random.domain.SelectedCategory;
import capstone.courseweb.random.dto.PlaceDto;
import capstone.courseweb.random.dto.RouteDto;
import capstone.courseweb.random.service.RouteService;
import capstone.courseweb.random.service.SearchByKeywordService;
import capstone.courseweb.user.domain.Member;
import capstone.courseweb.user.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PlaceSearchController {
    private final SearchByKeywordService searchService;
    private final RouteService routeService;


    @PostMapping("/search/category")
    public ResponseEntity<Map<String, Object>> searchPlaces(
            @RequestBody SelectedCategory selectedCategory
    ) throws JsonProcessingException { //, @RequestHeader("Authorization")String token


        log.info("Region: " + selectedCategory.getRegion());
        log.info("Categories: " + selectedCategory.getCategories());

        String[] categoriesFinal = new String[selectedCategory.getCategories().size()];

        for (int i = 0; i<selectedCategory.getCategories().size(); i++) {
            Random random = new Random();
            // random.nextInt(categories.get(i).size())
            categoriesFinal[i] = selectedCategory.getCategories().get(i).get(random.nextInt(selectedCategory.getCategories().get(i).size()));
        }



        SearchForm searchForm = new SearchForm(selectedCategory.getRegion(), categoriesFinal);
        List<PlaceDto> placeList = new ArrayList<>();

        log.info(searchForm.getLocal());
        for (String category : searchForm.getCategories()) {
            log.info("Category: " + category);
        }


        // 결과로 반환할 데이터 구조 생성
        Map<String, Object> response = new HashMap<>();


        for (int i = 0; i < searchForm.getCategories().length; i++) {
            String query = (i == 0) ? selectedCategory.getRegion() + ' ' +  searchForm.getCategories()[i] : searchForm.getCategories()[i];
            String x = (i == 0) ? null : placeList.get(i-1).getX();
            String y = (i == 0) ? null : placeList.get(i-1).getY();
            boolean isFirst = (i == 0);
            Optional<PlaceDto> op = searchService.searchPlacesByKeyword(query, x, y, isFirst);
            op.ifPresent(placeList::add);
            if(op.isEmpty()){
                response.put("info", 0);
                return ResponseEntity.ok(response);
            }

        }

        List<RouteDto> routes = routeService.findRoutesBetweenPlaces(placeList);

        List<List<String>> placeInfo = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<String> longitudes = new ArrayList<>();
        List<String> latitudes = new ArrayList<>();
        List<String> placeURL = new ArrayList<>();


        for (PlaceDto place : placeList) {
            names.add(place.getPlaceName());
            longitudes.add(place.getX());
            latitudes.add(place.getY());
            placeURL.add(place.getPlaceURL());
        }

        placeInfo.add(names);
        placeInfo.add(longitudes);
        placeInfo.add(latitudes);
        placeInfo.add(placeURL);

        System.out.println("placeInfo" + placeInfo);


        response.put("route", routes);
        response.put("info", placeInfo);

        System.out.println("플레이스 서치 response: " + response);

        //데이터 수정 예시.
        //Member member = memberOpt.get(); // Member 객체 가져오기
        //member.setName("현조");
        //memberRepository.save(member);


        return ResponseEntity.ok(response);
        //return ResponseEntity.ok(routes);
        //return ResponseEntity.ok(placeList);

    }


}