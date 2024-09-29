package capstone.courseweb.random.controller;

import capstone.courseweb.random.domain.SelectedCategory;
import capstone.courseweb.random.service.SearchCategoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PlaceSearchController {
    private final SearchCategoryService searchCategoryService;


    @PostMapping("/search/category")
    public ResponseEntity<Map<String, Object>> searchPlaces(
            @RequestBody SelectedCategory selectedCategory
    ) throws JsonProcessingException { //, @RequestHeader("Authorization")String token


        return ResponseEntity.ok(searchCategoryService.selectCategory(selectedCategory));
    }


}