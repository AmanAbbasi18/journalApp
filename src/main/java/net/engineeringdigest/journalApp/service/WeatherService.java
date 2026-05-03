package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.cache.AppCache;
import net.engineeringdigest.journalApp.constants.PlaceHolders;
import net.engineeringdigest.journalApp.response.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class WeatherService {
    @Value("${weather.api.key}")
    private String apikey;

    @Autowired
    private AppCache appCache;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisService redisService;

    public WeatherResponse getWeather(String city) {
        WeatherResponse weatherResponse = redisService.get("weather_of_" + city, WeatherResponse.class);
        if(weatherResponse != null) {   //runs if is expired or didn't even set
            return weatherResponse;
        }
        else {
            String finalAPI = appCache.appCache.get(AppCache.keys.WEATHER_API.toString()).replace(PlaceHolders.CITY , city).replace(PlaceHolders.API_KEY, apikey);

            ResponseEntity<WeatherResponse> response = restTemplate.exchange(finalAPI, HttpMethod.GET, null, WeatherResponse.class);
            WeatherResponse body = response.getBody();

            if(body != null) {
                redisService.set("weather_of_" + city , body, 500l);
            }
            return body;
        }
    }

}
