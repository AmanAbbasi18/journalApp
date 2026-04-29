package net.engineeringdigest.journalApp.Service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Disabled
    @Test
    void test() {
        redisTemplate.opsForValue().set("email" , "gmail@email.com");      //once saving 1st time still it will be saved in redis,can check after refresh
        Object email = redisTemplate.opsForValue().get("email");
    }
}
