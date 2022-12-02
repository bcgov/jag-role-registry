package ca.bc.gov.open.cso.controllers;

import ca.bc.gov.open.cso.*;
import ca.bc.gov.open.cso.configuration.SoapConfig;
import ca.bc.gov.open.cso.exceptions.ORDSException;
import ca.bc.gov.open.cso.models.OrdsErrorLog;
import ca.bc.gov.open.cso.services.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
@Slf4j
public class RoleController {

    @Value("${cso.host}")
    private String host = "https://127.0.0.1/";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final RedisService redisService;

    @Autowired
    public RoleController(
            RestTemplate restTemplate, ObjectMapper objectMapper, RedisService redisService) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.redisService = redisService;
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "getRolesForIdentifier")
    @ResponsePayload
    public GetRolesForIdentifierResponse getRolesForIdentifier(
            @RequestPayload GetRolesForIdentifier getRolesForIdentifier)
            throws JsonProcessingException {
        UserRoles userRoles = new UserRoles();
        /** temp code - only to test resp time without ORDS and Caching **/
        if (true) {
            var out = new GetRolesForIdentifierResponse();
            userRoles.setIdentifierType("test - no caching or ORDS!");
            out.setUserRoles(userRoles);
            return out;
        }
        try {
            userRoles =
                    redisService.fetchIdentifierResponseFromCache(
                            getRolesForIdentifier.getDomain(),
                            getRolesForIdentifier.getApplication(),
                            getRolesForIdentifier.getIdentifier(),
                            getRolesForIdentifier.getIdentifierType());
        } catch (RedisConnectionFailureException ex) {
            // Commonly caused by redis password error
            log.error("Redis Error");
            throw new ORDSException();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Redis Exception",
                                    "getRolesForIdentifier",
                                    ex.getMessage(),
                                    getRolesForIdentifier)));
            throw new ORDSException();
        }

        if (userRoles == null) {
            userRoles =
                    redisService.fetchIdentifierResponseFromDB(
                            getRolesForIdentifier.getDomain(),
                            getRolesForIdentifier.getApplication(),
                            getRolesForIdentifier.getIdentifier(),
                            getRolesForIdentifier.getIdentifierType());
        } else {
            log.info("Fetching from the Cache Success: \"getRolesForIdentifier\"");
        }
        var out = new GetRolesForIdentifierResponse();
        out.setUserRoles(userRoles);
        return out;
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "getRolesForApplication")
    @ResponsePayload
    public GetRolesForApplicationResponse getRolesForApplication(
            @RequestPayload GetRolesForApplication getRolesForApplication)
            throws JsonProcessingException {
        RoleResults roleResults = null;
        try {
            roleResults =
                    redisService.fetchApplicationResponseFromCache(
                            getRolesForApplication.getDomain(),
                            getRolesForApplication.getApplication(),
                            getRolesForApplication.getType());
        } catch (RedisConnectionFailureException ex) {
            // Commonly caused by redis password error
            log.error("Redis Error");
            throw new ORDSException();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Redis Exception",
                                    "getRolesForApplication",
                                    ex.getMessage(),
                                    getRolesForApplication)));
            throw new ORDSException();
        }

        if (roleResults == null) {
            roleResults =
                    redisService.fetchApplicationResponseFromDB(
                            getRolesForApplication.getDomain(),
                            getRolesForApplication.getApplication(),
                            getRolesForApplication.getType());
        } else {
            log.info("Fetching from the Cache Success: \"getRolesForApplication\"");
        }
        var out = new GetRolesForApplicationResponse();
        out.setRoleResults(roleResults);
        return out;
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "getRolesForIdentity")
    @ResponsePayload
    public GetRolesForIdentityResponse getRolesForIdentity(
            @RequestPayload GetRolesForIdentity getRolesForIdentity)
            throws JsonProcessingException {
        UserRoles userRoles = null;
        try {
            userRoles =
                    redisService.fetchIdentityResponseFromCache(
                            getRolesForIdentity.getDomain(),
                            getRolesForIdentity.getApplication(),
                            getRolesForIdentity.getUserIdentifier(),
                            getRolesForIdentity.getAccountIdentifier(),
                            getRolesForIdentity.getIdentifierType());
        } catch (RedisConnectionFailureException ex) {
            // Commonly caused by redis password error
            log.error("Redis Error");
            throw new ORDSException();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Redis Exception",
                                    "getRolesForIdentity",
                                    ex.getMessage(),
                                    getRolesForIdentity)));
            throw new ORDSException();
        }

        if (userRoles == null) {
            userRoles =
                    redisService.fetchIdentityResponseFromDB(
                            getRolesForIdentity.getDomain(),
                            getRolesForIdentity.getApplication(),
                            getRolesForIdentity.getUserIdentifier(),
                            getRolesForIdentity.getAccountIdentifier(),
                            getRolesForIdentity.getIdentifierType());
        } else {
            log.info("Fetching from the Cache Success: \"getRolesForIdentity\"");
        }
        var out = new GetRolesForIdentityResponse();
        out.setUserRoles(userRoles);
        return out;
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "changeRolesForIdentity")
    @ResponsePayload
    public void changeRolesForIdentity(
            @RequestPayload ChangeRolesForIdentity changeGetRolesForIdentity)
            throws JsonProcessingException {
        try {
            redisService.dropIdentityResponseFromCache(
                    changeGetRolesForIdentity.getDomain(),
                    changeGetRolesForIdentity.getApplication(),
                    changeGetRolesForIdentity.getUserIdentifier(),
                    changeGetRolesForIdentity.getAccountIdentifier(),
                    changeGetRolesForIdentity.getIdentifierType());
            log.info("Dropping from the Cache Success: \"RolesForIdentity\"");
        } catch (RedisConnectionFailureException ex) {
            // Commonly caused by redis password error
            log.error("Redis Error");
            throw new ORDSException();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Redis Exception",
                                    "changeGetRolesForIdentity",
                                    ex.getMessage(),
                                    changeGetRolesForIdentity)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "changeRolesForApplication")
    @ResponsePayload
    public void changeRolesForApplication(
            @RequestPayload ChangeRolesForApplication changeRolesForApplication)
            throws JsonProcessingException {
        try {
            redisService.dropApplicationResponseFromCache(
                    changeRolesForApplication.getDomain(),
                    changeRolesForApplication.getApplication(),
                    changeRolesForApplication.getType());
            log.info("Dropping from the Cache Success: \"RolesForApplication\"");
        } catch (RedisConnectionFailureException ex) {
            // Commonly caused by redis password error
            log.error("Redis Error");
            throw new ORDSException();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Redis Exception",
                                    "changeRolesForApplication",
                                    ex.getMessage(),
                                    changeRolesForApplication)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "changeRolesForIdentifier")
    @ResponsePayload
    public void changeRolesForIdentifier(
            @RequestPayload ChangeRolesForIdentifier changeRolesForIdentifier)
            throws JsonProcessingException {
        try {
            redisService.dropIdentifierResponseFromCache(
                    changeRolesForIdentifier.getDomain(),
                    changeRolesForIdentifier.getApplication(),
                    changeRolesForIdentifier.getIdentifier(),
                    changeRolesForIdentifier.getIdentifierType());
            log.info("Dropping from the Cache Success: \"RolesForIdentifier\"");
        } catch (RedisConnectionFailureException ex) {
            // Commonly caused by redis password error
            log.error("Redis Error");
            throw new ORDSException();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Redis Exception",
                                    "changeRolesForIdentifier",
                                    ex.getMessage(),
                                    changeRolesForIdentifier)));
            throw new ORDSException();
        }
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "clearCache")
    @ResponsePayload
    public void clearCache() throws JsonProcessingException {
        try {
            redisService.clearIdentifierResponseFromCache();
            redisService.clearApplicationResponseFromCache();
            redisService.clearIdentityResponseFromCache();

            log.info("Dropping All Cache Successfully");
        } catch (RedisConnectionFailureException ex) {
            // Commonly caused by redis password error
            log.error("Redis Error");
            throw new ORDSException();
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Redis Exception", "clearCache", ex.getMessage(), null)));
            throw new ORDSException();
        }
    }
}
