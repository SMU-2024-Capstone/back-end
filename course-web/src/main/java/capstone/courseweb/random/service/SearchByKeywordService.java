package capstone.courseweb.random.service;

import capstone.courseweb.random.dto.PlaceDto;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchByKeywordService {
    @Value("${kakao.api-key}")
    private String CLIENT_ID;
    private final String KAKAO_API_URL = "https://dapi.kakao.com/v2/local/search/keyword";
    private HttpEntity<String> httpEntity;

    @PostConstruct
    protected void init() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK "+CLIENT_ID);
        httpEntity = new HttpEntity<>(headers);
    }


    public Optional<PlaceDto>  searchPlacesByKeyword(String query, String x, String y, boolean isFirst) throws JsonProcessingException {
        URI tmp;
        if(isFirst) {
            tmp =
                    UriComponentsBuilder.fromHttpUrl(KAKAO_API_URL)
                            .queryParam("query", query)
                            .encode(StandardCharsets.UTF_8)
                            .build().toUri();
        }
        else {
            tmp =

                    UriComponentsBuilder.fromHttpUrl(KAKAO_API_URL)
                            .queryParam("query", query)
                            .queryParam("x", x)
                            .queryParam("y", y)
                            .queryParam("3000")
                            .encode(StandardCharsets.UTF_8)
                            .build().toUri();
        }

        Assert.notNull(query, "query");
        ResponseEntity<String> response = new RestTemplate().exchange(tmp, HttpMethod.GET, httpEntity, String.class);

        JSONObject jsonObject = new JSONObject(response.getBody().toString());
        JSONArray jsonArray = jsonObject.getJSONArray("documents");


        if (jsonArray.length() == 0) {
            return Optional.empty();

        }

        Random random = new Random();
        int randomInt = 0;

        if (jsonArray.length() >= 5) {
            randomInt = random.nextInt(5);
        }
        else {
            randomInt = random.nextInt(jsonArray.length());
        }
        JSONObject documentObj = jsonArray.getJSONObject(randomInt);
        PlaceDto placeDto = PlaceDto.builder()
                .placeName(documentObj.getString("place_name"))
                .addressName(documentObj.getString("address_name"))
                .x(documentObj.getString("x"))
                .y(documentObj.getString("y"))
                .placeURL(documentObj.getString("place_url"))
                .categoryName(documentObj.getString("category_name"))
                .distance(documentObj.getString("distance"))
                .build();

        return Optional.of(placeDto);
    }

}
