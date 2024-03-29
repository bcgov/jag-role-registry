package ca.bc.gov.open.cso.services;

import ca.bc.gov.open.cso.*;
import ca.bc.gov.open.cso.exceptions.ORDSException;
import ca.bc.gov.open.cso.models.OrdsErrorLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class RedisService {
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    @Value("${cso.host}")
    private String cso_host;

    @Value("${cso.caching}")
    public String caching = "enable";

    @Autowired
    public RedisService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Cacheable(
            cacheNames = "IdentifierCache",
            condition = "#root.target.caching=='enable'",
            unless = "#result == null")
    public UserRoles fetchIdentifierResponseFromCache(
            String domain, String application, String identifier, String identifierType) {
        return null;
    }

    @CachePut(cacheNames = "IdentifierCache", condition = "#root.target.caching=='enable'")
    public UserRoles fetchIdentifierResponseFromDB(
            String domain, String application, String identifier, String identifierType)
            throws JsonProcessingException {
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(cso_host + "roles/identifier")
                        .queryParam("domain", domain)
                        .queryParam("application", application)
                        .queryParam("identifier", identifier)
                        .queryParam("identifierType", identifierType);

        try {
            HttpEntity<UserRoles> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.GET,
                            new HttpEntity<>(new HttpHeaders()),
                            UserRoles.class);

            // Objectmapper is unable to deserialize an empty array into a tag
            // Therefore an empty object is added into the array as following
            if (resp.getBody().getRoles().isEmpty()) {
                List<RegisteredRole> l = resp.getBody().getRoles();
                RegisteredRole emptyRole = new RegisteredRole();
                l.add(emptyRole);
            }

            log.info("Fetch Success from DB: \"getRolesForIdentifier\"");

            return resp.getBody();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getRolesForIdentifier",
                                    ex.getMessage(),
                                    builder.toUriString())));
            throw new ORDSException();
        }
    }

    @Cacheable(
            cacheNames = "ApplicationCache",
            condition = "#root.target.caching=='enable'",
            unless = "#result == null")
    public RoleResults fetchApplicationResponseFromCache(
            String domain, String applicationNm, String type) {
        return null;
    }

    @CachePut(cacheNames = "ApplicationCache", condition = "#root.target.caching=='enable'")
    public RoleResults fetchApplicationResponseFromDB(
            String domain, String application, String type) throws JsonProcessingException {
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(cso_host + "roles/application")
                        .queryParam("domain", domain)
                        .queryParam("application", application)
                        .queryParam("type", type);

        try {
            HttpEntity<RoleResults> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.GET,
                            new HttpEntity<>(new HttpHeaders()),
                            RoleResults.class);

            log.info("Fetch Success from DB: \"getRolesForApplication\"");

            return resp.getBody();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getRolesForApplication",
                                    ex.getMessage(),
                                    builder.toUriString())));
            throw new ORDSException();
        }
    }

    @Cacheable(
            cacheNames = "IdentityCache",
            condition = "#root.target.caching=='enable'",
            unless = "#result == null")
    public UserRoles fetchIdentityResponseFromCache(
            String domain,
            String application,
            String userIdentifier,
            String accountIdentifier,
            String identifierType) {
        return null;
    }

    @CachePut(cacheNames = "IdentityCache", condition = "#root.target.caching=='enable'")
    public UserRoles fetchIdentityResponseFromDB(
            String domain,
            String application,
            String userIdentifier,
            String accountIdentifier,
            String identifierType)
            throws JsonProcessingException {
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(cso_host + "roles/identity")
                        .queryParam("domain", domain)
                        .queryParam("application", application)
                        .queryParam("userIdentifier", userIdentifier)
                        .queryParam("accountIdentifier", accountIdentifier)
                        .queryParam("identifierType", identifierType);

        try {
            HttpEntity<UserRoles> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.GET,
                            new HttpEntity<>(new HttpHeaders()),
                            UserRoles.class);

            // Objectmapper is unable to deserialize an empty array into a tag
            // Therefore an empty object is added into the array as following
            if (resp.getBody().getRoles().isEmpty()) {
                List<RegisteredRole> l = resp.getBody().getRoles();
                RegisteredRole emptyRole = new RegisteredRole();
                l.add(emptyRole);
            }

            log.info("Fetch Success from DB: \"getRolesForIdentity\"");

            return resp.getBody();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getRolesForIdentity",
                                    ex.getMessage(),
                                    builder.toUriString())));
            throw new ORDSException();
        }
    }

    @CacheEvict(cacheNames = "IdentityCache")
    public void dropIdentityResponseFromCache(
            String domain,
            String application,
            String userIdentifier,
            String accountIdentifier,
            String identifierType) {}

    @CacheEvict(cacheNames = "ApplicationCache")
    public void dropApplicationResponseFromCache(String domain, String application, String type) {}

    @CacheEvict(cacheNames = "IdentifierCache")
    public void dropIdentifierResponseFromCache(
            String domain, String application, String identifier, String identifierType) {}

    @CacheEvict(value = "IdentityCache", allEntries = true)
    public void clearIdentityResponseFromCache() {}

    @CacheEvict(value = "ApplicationCache", allEntries = true)
    public void clearApplicationResponseFromCache() {}

    @CacheEvict(value = "IdentifierCache", allEntries = true)
    public void clearIdentifierResponseFromCache() {}
}
