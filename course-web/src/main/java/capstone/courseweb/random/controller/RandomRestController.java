package capstone.courseweb.random.controller;

import capstone.courseweb.random.domain.SearchForm;
import capstone.courseweb.random.dto.PlaceDto;
import capstone.courseweb.random.service.SearchByKeywordService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public String search(
            @RequestParam String region,
            @RequestParam List<String> categories) {

        log.info("Region: " + region);
        log.info("Categories: " + categories);

        searchForm = new SearchForm(region, categories.toArray(new String[0]));

        log.info(searchForm.getLocal());
        for (String category : searchForm.getCategories()) {
            log.info("Category: " + category);
        }

        return "검색 요청 처리";
    }
    

    @GetMapping("/keyword/{query}")
    public ResponseEntity<PlaceDto> random(@PathVariable String query) throws JsonProcessingException {
        log.info("호출됨");
        placeDtos = searchByKeywordService.getPlaceByKeyword(query, null, null, false);
        pickedPlace = searchByKeywordService.random(placeDtos);
        log.info(placeDtos.toString());
        return ResponseEntity.ok(pickedPlace);
    }


}
