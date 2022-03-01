package ca.bc.gov.open.cso.controllers;

import ca.bc.gov.open.cso.*;
import ca.bc.gov.open.cso.configuration.SoapConfig;
import ca.bc.gov.open.cso.services.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
        UserRoles userRoles =
                redisService.fetchIdentifierResponseFromCache(
                        getRolesForIdentifier.getDomain(),
                        getRolesForIdentifier.getApplication(),
                        getRolesForIdentifier.getIdentifier(),
                        getRolesForIdentifier.getIdentifierType());

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
        RoleResults roleResults =
                redisService.fetchApplicationResponseFromCache(
                        getRolesForApplication.getDomain(),
                        getRolesForApplication.getApplication(),
                        getRolesForApplication.getType());

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
    public GetRolesForIdentityResponse getRolesForIdentity(
            @RequestPayload GetRolesForIdentity getRolesForIdentity)
            throws JsonProcessingException {
        UserRoles userRoles =
                redisService.fetchIdentityResponseFromCache(
                        getRolesForIdentity.getDomain(),
                        getRolesForIdentity.getApplication(),
                        getRolesForIdentity.getUserIdentifier(),
                        getRolesForIdentity.getAccountIdentifier(),
                        getRolesForIdentity.getIdentifierType());

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
}
