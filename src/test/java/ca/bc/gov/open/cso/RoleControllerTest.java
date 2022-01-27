package ca.bc.gov.open.cso;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.cso.controllers.RoleController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class RoleControllerTest {
    private RoleController roleController;

    @Mock private RestTemplate restTemplate;

    @Autowired private ObjectMapper objectMapper;

    @Autowired private MockMvc mockMvc;

    @Test
    public void getRolesForIdentifierTest() throws JsonProcessingException {
        var req = new GetRolesForIdentifier();
        req.setDomain("A");
        req.setApplication("A");
        req.setIdentifier("A");
        req.setIdentifierType("A");

        var resp = new UserRoles();
        resp.setDomain("A");
        resp.setApplication("A");
        resp.setIdentifier("A");
        resp.setIdentifierType("A");

        RegisteredRole rr = new RegisteredRole();
        rr.setCode("A");
        rr.setDescription("A");
        rr.setType("A");

        resp.setRoles(Collections.singletonList(rr));

        ResponseEntity<UserRoles> responseEntity = new ResponseEntity<>(resp, HttpStatus.OK);

        RoleController roleController = new RoleController(restTemplate, objectMapper);

        //     Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<UserRoles>>any()))
                .thenReturn(responseEntity);

        var out = roleController.getRolesForIdentifier(req);

        //     Assert response is correct
        Assertions.assertNotNull(out);
    }

    @Test
    public void getRolesForApplicationTest() throws JsonProcessingException {
        var req = new GetRolesForApplication();
        req.setDomain("A");
        req.setApplication("A");
        req.setType("A");

        var roleResults = new RoleResults();
        roleResults.setDomain("A");
        roleResults.setApplication("A");

        RegisteredRole rr = new RegisteredRole();
        rr.setCode("A");
        rr.setDescription("A");
        rr.setType("A");

        roleResults.setRoles(Collections.singletonList(rr));

        ResponseEntity<RoleResults> responseEntity =
                new ResponseEntity<>(roleResults, HttpStatus.OK);
        RoleController roleController = new RoleController(restTemplate, objectMapper);
        //     Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<RoleResults>>any()))
                .thenReturn(responseEntity);

        var out = roleController.getRolesForApplication(req);

        //     Assert response is correct
        Assertions.assertNotNull(out);
    }
}
