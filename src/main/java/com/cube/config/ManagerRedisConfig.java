package com.cube.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 这个redis配置仅仅用于manager层，其它层不要引用这个redis
 * 
 * @author phoenix
 * @date 2021年3月8日
 */
@Configuration
public class ManagerRedisConfig {

	/**
	 * 这个redis配置仅仅用于manager层，其它层不要引用这个redis
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param factory
	 * @return
	 */
	@Bean("dalRedis")
	public RedisTemplate<String, Object> dalRedis(RedisConnectionFactory factory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(factory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}
	
}
