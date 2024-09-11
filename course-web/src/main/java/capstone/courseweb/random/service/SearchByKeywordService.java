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
    private final String KAKAO_API_URL = "https://dapi.kakao.com/v2/local/search/keyword";
    //private static final String RADIUS = "15000";
    private HttpEntity<String> httpEntity;

    @PostConstruct
    protected void init() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK "+CLIENT_ID);
        httpEntity = new HttpEntity<>(headers);
    }


    public List<PlaceDto> searchPlacesByKeyword(String query, String x, String y, boolean isFirst) throws JsonProcessingException {
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
                            //.queryParam("radius","3000")
                            .encode(StandardCharsets.UTF_8)
                            .build().toUri();
        }

        Assert.notNull(query, "query");
        ResponseEntity<String> response = new RestTemplate().exchange(tmp, HttpMethod.GET, httpEntity, String.class);

        JSONObject jsonObject = new JSONObject(response.getBody().toString());
        System.out.println("카카오맵 api documents" + response);
        JSONArray jsonArray = jsonObject.getJSONArray("documents");

        List<PlaceDto> searchList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            JSONObject documentObj = jsonArray.getJSONObject(i);
            System.out.println("서치바이키워드서비스 카카오에서 장소 정보 받아오는: " + documentObj);
            searchList.add(
                    PlaceDto.builder()
                            .placeName(documentObj.getString("place_name"))
                            .addressName(documentObj.getString("address_name"))
                            .x(documentObj.getString("x"))
                            .y(documentObj.getString("y"))
                            .placeURL(documentObj.getString("place_url"))
                            .categoryName(documentObj.getString("category_name"))
                            //.distance(documentObj.getString("distance"))
                            .build());
        }

        System.out.println("장소 사이 거리: " + searchList.get(0).getDistance());
        return searchList;
    }

    public PlaceDto getRandomPlace(List<PlaceDto> places) throws JsonProcessingException {
        Random random = new Random();
        return places.get(random.nextInt(places.size()));
    }

}
