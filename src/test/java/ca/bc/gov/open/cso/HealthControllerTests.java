package ca.bc.gov.open.cso;

import static org.mockito.Mockito.when;

import ca.bc.gov.open.cso.controllers.HealthController;
import ca.bc.gov.open.cso.exceptions.ORDSException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class HealthControllerTests {

    @Mock private RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void getHealthTest() throws IOException {
        HealthController healthController = new HealthController(restTemplate, objectMapper);

        var hr = new GetHealthResponse();
        hr.setAppid("A");
        hr.setCompatibility("A");
        hr.setHost("A");
        hr.setMethod("A");
        hr.setVersion("A");
        hr.setInstance("A");
        hr.setStatus("A");

        ResponseEntity<GetHealthResponse> responseEntity = new ResponseEntity<>(hr, HttpStatus.OK);

        //     Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<GetHealthResponse>>any()))
                .thenReturn(responseEntity);

        GetHealthResponse out = healthController.getHealth(new GetHealth());

        Assertions.assertNotNull(out);
    }

    @Test
    public void getPingTest() throws JsonProcessingException {
        //        Only needed for log test otherwise required refactor
        HealthController healthController = new HealthController(restTemplate, objectMapper);

        var pr = new GetPingResponse();
        pr.setStatus("A");

        ResponseEntity<GetPingResponse> responseEntity = new ResponseEntity<>(pr, HttpStatus.OK);

        //     Set up to mock ords response
        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<GetPingResponse>>any()))
                .thenReturn(responseEntity);

        GetPingResponse out = healthController.getPing(new GetPing());

        Assertions.assertNotNull(out);
    }

    @Test
    public void badOrdsGetPing() {

        HealthController healthController = new HealthController(restTemplate, objectMapper);

        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Object>>any()))
                .thenThrow(new RestClientException("BAD"));

        Assertions.assertThrows(ORDSException.class, () -> healthController.getPing(new GetPing()));
    }

    @Test
    public void badOrdsGetHealth() {

        HealthController healthController = new HealthController(restTemplate, objectMapper);

        when(restTemplate.exchange(
                        Mockito.any(String.class),
                        Mockito.eq(HttpMethod.GET),
                        Mockito.<HttpEntity<String>>any(),
                        Mockito.<Class<Object>>any()))
                .thenThrow(new RestClientException("BAD"));

        Assertions.assertThrows(
                ORDSException.class, () -> healthController.getHealth(new GetHealth()));
    }
}
