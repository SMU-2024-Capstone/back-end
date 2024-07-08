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
public class RandomRestController {

    private SearchForm searchForm;

    private List<PlaceDto> placeDtos;
    private PlaceDto pickedPlace;
    private final SearchByKeywordService searchByKeywordService;


    @GetMapping("/search/category")
    public ResponseEntity<List<PlaceDto>> search(
            @RequestParam String region,
            @RequestParam List<String> categories) throws JsonProcessingException {

        log.info("Region: " + region);
        log.info("Categories: " + categories);

        searchForm = new SearchForm(region, categories.toArray(new String[0]));
        placeDtos = new ArrayList<>();

        log.info(searchForm.getLocal());
        for (String category : searchForm.getCategories()) {
            log.info("Category: " + category);
        }

        for (int i = 0; i < searchForm.getCategories().length; i++) {
            if(i == 0) {
                placeDtos.add(searchByKeywordService.random(
                        searchByKeywordService.getPlaceByKeyword(region + searchForm.getCategories()[i],
                                null, null, true)));
            }
            else {
                placeDtos.add(searchByKeywordService.random(
                        searchByKeywordService.getPlaceByKeyword(searchForm.getCategories()[i],
                                placeDtos.get(i-1).getX(), placeDtos.get(i-1).getY(), false)));

            }
        }

        return ResponseEntity.ok(placeDtos);

    }


    /*
    @GetMapping("/keyword/{query}")
    public ResponseEntity<PlaceDto> random(@PathVariable String query) throws JsonProcessingException {
        log.info("호출됨");
        placeDtos = searchByKeywordService.getPlaceByKeyword(query, null, null, false);
        pickedPlace = searchByKeywordService.random(placeDtos);
        log.info(placeDtos.toString());
        return ResponseEntity.ok(pickedPlace);
    }

     */


}
