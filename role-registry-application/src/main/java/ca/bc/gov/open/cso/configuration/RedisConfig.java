package ca.bc.gov.open.cso.configuration;

import ca.bc.gov.open.cso.RoleResults;
import ca.bc.gov.open.cso.UserRoles;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@EnableCaching
@Configuration
public class RedisConfig {
    @Value("${redis.host}")
    private String host = "";

    @Value("${redis.port}")
    private String port = "8080";

    @Value("${redis.redis-auth-pass}")
    private String password = "";

    @Value("${redis.ttl}")
    private String ttl = "60";

    private final ObjectMapper objectMapper;

    @Autowired
    public RedisConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public JedisConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration =
                new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(Integer.parseInt(port));
        redisStandaloneConfiguration.setDatabase(0);
        redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
        JedisClientConfigurationBuilder jedisClientConfiguration =
                JedisClientConfiguration.builder();
        jedisClientConfiguration.connectTimeout(Duration.ofSeconds(60));
        JedisConnectionFactory jedisConnectionFactory =
                new JedisConnectionFactory(
                        redisStandaloneConfiguration, jedisClientConfiguration.build());
        return jedisConnectionFactory;
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheConfiguration() {
        // Serializing RoleResults for getRolesForApplication
        Jackson2JsonRedisSerializer<RoleResults> roleResultsSerializer =
                new Jackson2JsonRedisSerializer<>(RoleResults.class);
        roleResultsSerializer.setObjectMapper(objectMapper);

        // Serializing UserRoles for getRolesForIdentifier and getRolesForIdentity
        Jackson2JsonRedisSerializer<UserRoles> userRolesSerializer =
                new Jackson2JsonRedisSerializer<>(UserRoles.class);
        userRolesSerializer.setObjectMapper(objectMapper);

        return (builder) -> {
            Map<String, RedisCacheConfiguration> configurationMap = new HashMap<>();
            RedisCacheConfiguration applicationConfiguration =
                    RedisCacheConfiguration.defaultCacheConfig()
                            .entryTtl(Duration.ofSeconds(Integer.parseInt(ttl)))
                            .serializeValuesWith(
                                    RedisSerializationContext.SerializationPair.fromSerializer(
                                            roleResultsSerializer));
            RedisCacheConfiguration identityConfiguration =
                    RedisCacheConfiguration.defaultCacheConfig()
                            .entryTtl(Duration.ofSeconds(Integer.parseInt(ttl)))
                            .serializeValuesWith(
                                    RedisSerializationContext.SerializationPair.fromSerializer(
                                            userRolesSerializer));
            RedisCacheConfiguration identifierConfiguration =
                    RedisCacheConfiguration.defaultCacheConfig()
                            .entryTtl(Duration.ofSeconds(Integer.parseInt(ttl)))
                            .serializeValuesWith(
                                    RedisSerializationContext.SerializationPair.fromSerializer(
                                            userRolesSerializer));
            ;

            configurationMap.put("ApplicationCache", applicationConfiguration);
            configurationMap.put("IdentityCache", identityConfiguration);
            configurationMap.put("IdentifierCache", identifierConfiguration);

            builder.withInitialCacheConfigurations(configurationMap);
        };
    }
}
