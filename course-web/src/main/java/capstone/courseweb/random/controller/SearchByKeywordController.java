package capstone.courseweb.random.controller;

import capstone.courseweb.random.domain.SearchForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
public class SearchByKeywordController {

    /*
    private SearchForm searchForm;


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

     */
}
