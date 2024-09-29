package capstone.courseweb.random.service;

import capstone.courseweb.random.dto.PlaceDto;
import capstone.courseweb.random.dto.RouteDto;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        List<RouteDto> routes = new ArrayList<>();

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


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to get route from ODsay API", e);
        }

        String responseBody = response.body();

        JSONObject jsonObject = new JSONObject(responseBody);


        if (jsonObject.has("error")) {
            if (jsonObject.get("error") instanceof JSONObject) {
                JSONObject errorJson = jsonObject.getJSONObject("error");
                if (errorJson.getString("code").equals("-98")) {
                    jsonObject.put("result", "도보");
                }
            } 
            return jsonObject.toString();

        }
        return response.body();
    }

    private RouteDto parseRouteResponse(String response, PlaceDto from, PlaceDto to) {
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.has("error")) {

            StringBuilder routeDescription = new StringBuilder();
            routeDescription.append("도보 ").append(to.getDistance()).append("m");

            return RouteDto.builder()
                    .routeDescription(routeDescription.toString())
                    .distance(to.getDistance())
                    .duration(null)
                    .build();
        }

        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray pathArray = result.getJSONArray("path");

        //첫번째 경로만 선택
        JSONObject firstPath = pathArray.getJSONObject(0);
        int totalTime = firstPath.getJSONObject("info").getInt("totalTime");
        int distance = firstPath.getJSONObject("info").getInt("totalDistance");

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
                if(trafficType == 0) {
                }
                if (subPath.getInt("sectionTime") != 0) {
                    routeDescription.append("도보 ").append(subPath.getInt("sectionTime")).append("분").append("<br>");
                }
            }
        }

        return RouteDto.builder()
                .routeDescription(routeDescription.toString())
                .distance(String.valueOf(distance))
                .duration(String.valueOf(totalTime))
                .build();
    }

}
