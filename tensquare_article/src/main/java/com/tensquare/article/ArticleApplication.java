package com.tensquare.article;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import util.IdWorker;

import javax.annotation.Resource;

@SpringBootApplication
public class ArticleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArticleApplication.class, args);
	}

	@Bean
	public IdWorker idWorkker(){
		return new IdWorker(1, 1);
	}

	/**
	 * 解决key 值乱码
	 * @param redisTemplate
	 */
	@Resource
	public void redisKeySerializer(RedisTemplate redisTemplate){
		redisTemplate.setKeySerializer(new StringRedisSerializer());
	}
}
