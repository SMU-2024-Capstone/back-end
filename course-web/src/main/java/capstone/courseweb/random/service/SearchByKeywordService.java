package capstone.courseweb.random.service;

import capstone.courseweb.random.dto.PlaceDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.lang.Assert;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchByKeywordService {
    @Value("${kakao.api-key}")
    private String CLIENT_ID;

    private final String uri = "https://dapi.kakao.com/v2/local/search/keyword";

    private HttpEntity<String> httpEntity;

    @PostConstruct
    protected void init() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK "+CLIENT_ID);
        httpEntity = new HttpEntity<>(headers);
    }

    public List<PlaceDto> getPlaceByKeyword(String query) throws JsonProcessingException {
        URI tmp = UriComponentsBuilder.fromHttpUrl(uri)
                .queryParam("query", query)
                .encode(StandardCharsets.UTF_8)
                .build().toUri();

        Assert.notNull(query, "query");
        ResponseEntity<String> response = new RestTemplate().exchange(tmp, HttpMethod.GET, httpEntity, String.class);

        JSONObject jsonObject = new JSONObject(response.getBody().toString());
        JSONArray jsonArray = jsonObject.getJSONArray("documents");

        List<PlaceDto> searchList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            JSONObject documentObj = jsonArray.getJSONObject(i);
            searchList.add(
                    PlaceDto.builder()
                            .placeName(documentObj.getString("place_name"))
                            .addressName(documentObj.getString("address_name"))
                            .x(documentObj.getString("x"))
                            .y(documentObj.getString("y"))
                            .placeURL(documentObj.getString("place_url"))
                            .categoryName(documentObj.getString("category_name"))
                            .build());
        }
        return searchList;
    }

    public PlaceDto random(List<PlaceDto> placeDtos) throws JsonProcessingException {
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());

        int r = random.nextInt(placeDtos.size());

        return placeDtos.get(r);
    }

}
