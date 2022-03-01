package ca.bc.gov.open.cso.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration.JedisClientConfigurationBuilder;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@EnableCaching
@Configuration
public class RedisConfig {
    @Value("${redis.host}")
    private String host;
    @Value("${redis.port}")
    private String port;
    @Value("${redis.redis-auth-pass}")
    private String password;

    @Bean
    public JedisConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(Integer.parseInt(port));
        redisStandaloneConfiguration.setDatabase(0);
        // redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
        JedisClientConfigurationBuilder jedisClientConfiguration = JedisClientConfiguration.builder();
        jedisClientConfiguration.connectTimeout(Duration.ofSeconds(60));
        JedisConnectionFactory jedisConnectionFactory =
                new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration.build());
        return jedisConnectionFactory;
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheConfiguration() {
        return (builder) -> {
            Map<String, RedisCacheConfiguration> configurationMap = new HashMap<>();
            RedisCacheConfiguration applicationConfiguration =
                    RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(5));
            RedisCacheConfiguration identityConfiguration =
                    RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(5));
            RedisCacheConfiguration identifierConfiguration =
                    RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(5));

            configurationMap.put("ApplicationCache", applicationConfiguration);
            configurationMap.put("IdentityCache", identityConfiguration);
            configurationMap.put("IdentifierCache", identifierConfiguration);

            builder.withInitialCacheConfigurations(configurationMap);
        };
    }
}
