package ca.bc.gov.open.cso;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ca.bc.gov.open.cso.controllers.HealthController;
import ca.bc.gov.open.cso.controllers.RoleController;
import ca.bc.gov.open.cso.exceptions.ORDSException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class OrdsErrorTests {
    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Mock private RestTemplate restTemplate;

    @Test
    public void pingOrdsFail() {
        HealthController healthController = new HealthController(restTemplate, objectMapper);

        Assertions.assertThrows(ORDSException.class, () -> healthController.getPing(new GetPing()));
    }

    @Test
    public void healthOrdsFail() {
        HealthController healthController = new HealthController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class, () -> healthController.getHealth(new GetHealth()));
    }

    @Test
    public void getRolesForIdentifierOrdsFail() {
        RoleController roleController = new RoleController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () -> roleController.getRolesForIdentifier(new GetRolesForIdentifier()));
    }

    @Test
    public void getRolesForApplicationOrdsFail() {
        RoleController roleController = new RoleController(restTemplate, objectMapper);

        Assertions.assertThrows(
                ORDSException.class,
                () -> roleController.getRolesForApplication(new GetRolesForApplication()));
    }

    @Test
    public void securityTestFail_Then401() throws Exception {
        var response =
                mockMvc.perform(post("/ws").contentType(MediaType.TEXT_XML))
                        .andExpect(status().is4xxClientError())
                        .andReturn();
        Assertions.assertEquals(
                HttpStatus.UNAUTHORIZED.value(), response.getResponse().getStatus());
    }
}
