package capstone.courseweb.random.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class SearchByKeywordService {
    @Value("${kakao.api-key}")
    private String CLIENT_ID;

    private final String url = "https://dapi.kakao.com/v2/local/search/keyword";

    @GetMapping("/category/search")
    public void search(@RequestParam("nCategory") int nCategory) {

    }
}
