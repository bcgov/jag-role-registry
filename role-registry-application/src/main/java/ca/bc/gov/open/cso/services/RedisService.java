package ca.bc.gov.open.cso.services;

import ca.bc.gov.open.cso.UserRoles;
import ca.bc.gov.open.cso.exceptions.ORDSException;
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

    @Value("${cso.host}")
    private String cso_host;

    @Autowired
    public RedisService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Cacheable(cacheNames = "IdentifierCache", unless = "#result == null")
    private UserRoles fetchIdentifierResponseFromCache(String domain,
                                                       String applicationNm,
                                                       String identifierNm,
                                                       String identifierType) {
        return null;
    }

    @CachePut(cacheNames =  "IdentifierCache")
    private UserRoles fetchIdentifierResponseFromDB(String domain,
                                                    String applicationNm,
                                                    String identifierNm,
                                                    String identifierType) {
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(cso_host + "roles/identifier")
                        .queryParam("domain", domain)
                        .queryParam("applicationNm", applicationNm)
                        .queryParam("identifierNm", identifierNm)
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
            log.error("Fetch Failed from DB - Error received from ORDS: \"getRolesForIdentifier\"");
            throw new ORDSException();
        }
    }
}
