package com.example.ans;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableCaching
public class AnimalNameService {

    public static void main(String[] args) {
        SpringApplication.run(AnimalNameService.class, args);
    }

//    @Bean
//    public LettuceConnectionFactory connectionFactory() {
//        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration("redis-master", Set.of("127.0.0.1:26379"));
//        return new LettuceConnectionFactory(redisSentinelConfiguration);
//    }
}

@RestController
@RequestMapping("/api")
@Slf4j
class AnimalNameResource {

    private final Map<Integer, String> animalNamesLUT;
    private int animalCounter;

    public AnimalNameResource() throws IOException {
        InputStream inputStream = new ClassPathResource("/animals.txt").getInputStream();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            List<String> animalNames = reader.lines().collect(Collectors.toList());
            animalNamesLUT = animalNames.stream().collect(Collectors.toMap(this::nextInt, Function.identity()));
        }

        log.debug("Loaded {} names", animalNamesLUT.size());
    }

    private Integer nextInt(String value) {
        return animalCounter++;
    }

    @GetMapping(path = "/animal/{id}")
    @ResponseBody
    @Cacheable(value = "nameCache")
    public String findName(@PathVariable("id") int id) {
        log.info("Cache miss on id - {}", id);
        return animalNamesLUT.get(id);

    }
}



