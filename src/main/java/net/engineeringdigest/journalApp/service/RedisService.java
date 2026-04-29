package net.engineeringdigest.journalApp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.response.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    //2 method to set in redis and get from redis
    public <T>  T get(String key, Class<T> weatherResponseClass) {
        try {
            Object o = redisTemplate.opsForValue().get(key);    //we couldv'e converted the generic response directly to WeatherResponse by WeatherResponse o = ...; but then if some other time used then for that class have to create a seperate template
            ObjectMapper mapper = new ObjectMapper();           // mapper use kiya he kyunki JSON se JAVA deserialie bhi karna he
            return mapper.readValue(o.toString() , weatherResponseClass);
        } catch(Exception e) {
            log.error("Exception ", e);
            return null;
        }
    }

    public void set(String key, Object o, Long ttl) {    //ttl - timetolive
        try {
            //we are only manually serializing the 'o'(object) because redis will try to convert the object into strig but will not be able to ,will give some exception/error
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonValue = objectMapper.writeValueAsString(o);
            redisTemplate.opsForValue().set(key, jsonValue, ttl, TimeUnit.SECONDS);  //way to set key with single value but expies after a certain time
        } catch(Exception e) {
            log.error("Exception ", e);
        }
    }
}
