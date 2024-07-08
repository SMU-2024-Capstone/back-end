package capstone.courseweb.random.controller;

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
@RequestMapping("/api/random")
public class RandomRestController {

    private List<PlaceDto> placeDtos;
    private final SearchByKeywordService searchByKeywordService;


    @GetMapping("/keyword/{query}")
    public ResponseEntity<List<PlaceDto>> random(@PathVariable String query) throws JsonProcessingException {
        log.info("호출됨");
        placeDtos = searchByKeywordService.getPlaceByKeyword(query);
        log.info(placeDtos.toString());
        return ResponseEntity.ok(searchByKeywordService.getPlaceByKeyword(query));
    }

}
