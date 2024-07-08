package capstone.courseweb.random.controller;

import capstone.courseweb.random.domain.SearchForm;
import capstone.courseweb.random.dto.PlaceDto;
import capstone.courseweb.random.service.SearchByKeywordService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PlaceSearchController {
    private final SearchByKeywordService searchService;


    @GetMapping("/search/category")
    public ResponseEntity<List<PlaceDto>> searchPlaces(
            @RequestParam String region,
            @RequestParam List<String> categories) throws JsonProcessingException {

        log.info("Region: " + region);
        log.info("Categories: " + categories);

        SearchForm searchForm = new SearchForm(region, categories.toArray(new String[0]));
        List<PlaceDto> placeList = new ArrayList<>();

        log.info(searchForm.getLocal());
        for (String category : searchForm.getCategories()) {
            log.info("Category: " + category);
        }

        for (int i = 0; i < searchForm.getCategories().length; i++) {
            String query = (i == 0) ? region + searchForm.getCategories()[i] : searchForm.getCategories()[i];
            String x = (i == 0) ? null : placeList.get(i-1).getX();
            String y = (i == 0) ? null : placeList.get(i-1).getY();
            boolean isFirst = (i == 0);

            placeList.add(searchService.getRandomPlace(
                    searchService.searchPlacesByKeyword(query, x, y, isFirst)));
        }

        return ResponseEntity.ok(placeList);

    }


}
