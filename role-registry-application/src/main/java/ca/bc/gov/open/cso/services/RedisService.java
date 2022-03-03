package ca.bc.gov.open.cso.services;

import ca.bc.gov.open.cso.*;
import ca.bc.gov.open.cso.exceptions.ORDSException;
import ca.bc.gov.open.cso.models.OrdsErrorLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    public RedisService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Cacheable(cacheNames = "IdentifierCache", unless = "#result == null")
    public UserRoles fetchIdentifierResponseFromCache(
            String domain, String application, String identifier, String identifierType) {
        return null;
    }

    @CachePut(cacheNames = "IdentifierCache")
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
            log.info("Fetch Success from DB: \"getRolesForIdentifier\"");
            return resp.getBody();
        } catch (Exception ex) {
            var getRolesForIdentifier = new GetRolesForIdentifier();
            getRolesForIdentifier.setDomain(domain);
            getRolesForIdentifier.setApplication(application);
            getRolesForIdentifier.setIdentifier(identifier);
            getRolesForIdentifier.setIdentifierType(identifierType);
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getRolesForIdentifier",
                                    ex.getMessage(),
                                    getRolesForIdentifier)));
            throw new ORDSException();
        }
    }

    @Cacheable(cacheNames = "ApplicationCache", unless = "#result == null")
    public RoleResults fetchApplicationResponseFromCache(
            String domain, String applicationNm, String type) {
        return null;
    }

    @CachePut(cacheNames = "ApplicationCache")
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
            var getRolesForApplication = new GetRolesForApplication();
            getRolesForApplication.setDomain(domain);
            getRolesForApplication.setApplication(application);
            getRolesForApplication.setType(type);
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getRolesForApplication",
                                    ex.getMessage(),
                                    getRolesForApplication)));
            throw new ORDSException();
        }
    }

    @Cacheable(cacheNames = "IdentityCache", unless = "#result == null")
    public UserRoles fetchIdentityResponseFromCache(
            String domain,
            String application,
            String userIdentifier,
            String accountIdentifier,
            String identifierType) {
        return null;
    }

    @CachePut(cacheNames = "IdentityCache")
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
            log.info("Fetch Success from DB: \"getRolesForIdentity\"");
            return resp.getBody();
        } catch (Exception ex) {
            var getRolesForIdentity = new GetRolesForIdentity();
            getRolesForIdentity.setDomain(domain);
            getRolesForIdentity.setApplication(application);
            getRolesForIdentity.setUserIdentifier(userIdentifier);
            getRolesForIdentity.setAccountIdentifier(accountIdentifier);
            getRolesForIdentity.setIdentifierType(identifierType);
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getRolesForIdentity",
                                    ex.getMessage(),
                                    getRolesForIdentity)));
            throw new ORDSException();
        }
    }
}
