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
import capstone.courseweb.random.service.SearchCategoryService;
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
    private final SearchCategoryService searchCategoryService;


    @PostMapping("/search/category")
    public ResponseEntity<Map<String, Object>> searchPlaces(
            @RequestBody SelectedCategory selectedCategory
    ) throws JsonProcessingException { //, @RequestHeader("Authorization")String token

        log.info("Region: " + selectedCategory.getRegion());
        log.info("Categories: " + selectedCategory.getCategories());

        return ResponseEntity.ok(searchCategoryService.selectCategory(selectedCategory));
    }


}