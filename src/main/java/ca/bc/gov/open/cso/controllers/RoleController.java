package ca.bc.gov.open.cso.controllers;

import ca.bc.gov.open.cso.*;
import ca.bc.gov.open.cso.configuration.SoapConfig;
import ca.bc.gov.open.cso.exceptions.ORDSException;
import ca.bc.gov.open.cso.models.OrdsErrorLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
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

    @Autowired
    public RoleController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "getRolesForIdentifier")
    @ResponsePayload
    public GetRolesForIdentifierResponse getRolesForIdentifier(
            @RequestPayload GetRolesForIdentifier getRolesForIdentifier)
            throws JsonProcessingException {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "roles/identifier")
                        .queryParam("domain", getRolesForIdentifier.getDomain())
                        .queryParam("applicationNm", getRolesForIdentifier.getApplication())
                        .queryParam("identifierNm", getRolesForIdentifier.getIdentifier())
                        .queryParam("identifierType", getRolesForIdentifier.getIdentifierType());

        try {
            HttpEntity<UserRoles> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.GET,
                            new HttpEntity<>(new HttpHeaders()),
                            UserRoles.class);

            var out = new GetRolesForIdentifierResponse();
            out.setUserRoles(resp.getBody());
            return out;
        } catch (Exception ex) {
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

    @PayloadRoot(namespace = SoapConfig.SOAP_NAMESPACE, localPart = "getRolesForApplication")
    @ResponsePayload
    public GetRolesForApplicationResponse getRolesForApplication(
            @RequestPayload GetRolesForApplication getRolesForApplication)
            throws JsonProcessingException {
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(host + "roles/application")
                        .queryParam("domain", getRolesForApplication.getDomain())
                        .queryParam("applicationNm", getRolesForApplication.getApplication())
                        .queryParam("identifierType", getRolesForApplication.getType());
        try {
            HttpEntity<RoleResults> resp =
                    restTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.GET,
                            new HttpEntity<>(new HttpHeaders()),
                            RoleResults.class);

            var out = new GetRolesForApplicationResponse();
            out.setRoleResults(resp.getBody());
            return out;
        } catch (Exception ex) {
            log.error(
                    objectMapper.writeValueAsString(
                            new OrdsErrorLog(
                                    "Error received from ORDS",
                                    "getDigitalDisplayCourtList",
                                    ex.getMessage(),
                                    getRolesForApplication)));
            throw new ORDSException();
        }
    }
}
