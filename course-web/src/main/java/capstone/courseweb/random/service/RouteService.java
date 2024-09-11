package capstone.courseweb.random.service;

import capstone.courseweb.random.dto.PlaceDto;
import capstone.courseweb.random.dto.RouteDto;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RouteService {
    @Value("${odsay.api-key}")
    private String ODSAY_API_KEY;
    private final String ODSAY_ROUTE_URL = "https://api.odsay.com/v1/api/searchPubTransPathT";
    private final HttpClient httpClient = HttpClient.newBuilder().build();

    public List<RouteDto> findRoutesBetweenPlaces(List<PlaceDto> places) {
        List<RouteDto> routes = new ArrayList<>(); // 장소 사이의 경로들 JSON

        for (int i = 0; i < places.size() - 1; i++) {
            PlaceDto from = places.get(i);
            PlaceDto to = places.get(i+1);



            String response = getRoute(from.getX(), from.getY(), to.getX(), to.getY());
            RouteDto route = parseRouteResponse(response, from, to);
            routes.add(route);
        }

        return routes;
    }

    private String getRoute(String fromX, String fromY, String toX, String toY) {
        String url = UriComponentsBuilder.fromHttpUrl(ODSAY_ROUTE_URL)
                .queryParam("SX", fromX)
                .queryParam("SY", fromY)
                .queryParam("EX", toX)
                .queryParam("EY", toY)
                .queryParam("apiKey", ODSAY_API_KEY)
                .toUriString();

        System.out.println("Generated URL: " + url);


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("에러 메세지:" + e);
            log.error("Error while calling ODsay API", e);
            throw new RuntimeException("Failed to get route from ODsay API", e);
        }

        // response body를 JSON으로 파싱 후 에러 코드 확인
        String responseBody = response.body();
        System.out.println("Respnse Body: " + responseBody);

        JSONObject jsonObject = new JSONObject(responseBody);

        //String result = responseBody;

        // 에러 코드가 -98인 경우 result에 "도보" 추가
        if (jsonObject.has("error")) {
            System.out.println("Error field exists");

            if (jsonObject.get("error") instanceof JSONObject) {
                JSONObject errorJson = jsonObject.getJSONObject("error");
                if (errorJson.getString("code").equals("-98")) {
                    jsonObject.put("result", "도보");
                }
            } else if (jsonObject.get("error") instanceof JSONArray) {
                JSONArray errorAJson = jsonObject.getJSONArray("error");
                System.out.println("Error JSONObject: " + errorAJson.getJSONObject(0).toString());
            }
            return jsonObject.toString();


            /*if (jsonObject.get("error") instanceof JSONObject ) {
                if (jsonObject.getString("code").equals("-98")) {
                    jsonObject.put("result", "도보");
                    return jsonObject.toString();
                }
            } else if (jsonObject.get("error") instanceof JSONArray) {
                JSONArray errorAJson = jsonObject.getJSONArray("error");
                JSONObject errorJson = errorAJson.getJSONObject(0);
                // JSONObject errorJson = jsonObject.getJSONObject("error");
                System.out.println("Error JSONObject: " + errorJson.toString());
            }*/
            /*JSONArray errorAJson = jsonObject.getJSONArray("error");
            JSONObject errorJson = errorAJson.getJSONObject(0);
            // JSONObject errorJson = jsonObject.getJSONObject("error");
            System.out.println("Error JSONObject: " + errorJson.toString());*/

            /*if (errorJson.getString("code").equals("-98")) {
                jsonObject.put("result", "도보");
                return jsonObject.toString();
            }*/

            //System.out.println("에러코드 -98: " + result);
        }

        // 수정된 JSON 객체를 문자열로 변환하여 반환
        //System.out.println(response.body());
        return response.body();
    }

    private RouteDto parseRouteResponse(String response, PlaceDto from, PlaceDto to) {
        JSONObject jsonObject = new JSONObject(response);
        log.info(jsonObject.toString());
        if (jsonObject.has("error")) {

            StringBuilder routeDescription = new StringBuilder();
            routeDescription.append("도보 ").append(to.getDistance()).append("m");

            return RouteDto.builder()
                    //.startPlace(from.getPlaceName())
                    //.sx(from.getX())
                    //.sy(from.getY())
                    //.endPlace(to.getPlaceName())
                    //.ex(to.getX())
                    //.ey(to.getY())
                    .routeDescription(routeDescription.toString())
                    .distance(to.getDistance()) //총 거리
                    .duration(null) //총 시간
                    //.startPlaceURL(from.getPlaceURL())
                    //.endPlaceURL(to.getPlaceURL())
                    .build();
        }

        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray pathArray = result.getJSONArray("path");

        //첫번째 경로만 선택?
        JSONObject firstPath = pathArray.getJSONObject(0);
        int totalTime = firstPath.getJSONObject("info").getInt("totalTime");
        int distance = firstPath.getJSONObject("info").getInt("totalDistance"); //info.totalDistance
        log.info("distance: " + distance);

        JSONArray subPathArray = firstPath.getJSONArray("subPath");
        StringBuilder routeDescription = new StringBuilder();


        for (int i = 0; i< subPathArray.length(); i++) {
            JSONObject subPath = subPathArray.getJSONObject(i);
            int trafficType = subPath.getInt("trafficType");

            //교통 수단과 시간
            if (trafficType == 1) {
                routeDescription.append("지하철 ").append(subPath.getInt("sectionTime")).append("분").append("<br>");
            } else if (trafficType == 2) {
                routeDescription.append("버스 ").append(subPath.getInt("sectionTime")).append("분").append("<br>");
            } else {
                routeDescription.append("도보 ").append(subPath.getInt("sectionTime")).append("분").append("<br>");
            }
        }

        return RouteDto.builder()
                //.startPlace(from.getPlaceName())
                //.sx(from.getX())
                //.sy(from.getY())
                //.endPlace(to.getPlaceName())
                //.ex(to.getX())
                //.ey(to.getY())
                .routeDescription(routeDescription.toString())
                .distance(String.valueOf(distance)) //총 거리
                .duration(String.valueOf(totalTime)) //총 시간
                //.startPlaceURL(from.getPlaceURL())
                //.endPlaceURL(to.getPlaceURL())
                .build();
    }

}
