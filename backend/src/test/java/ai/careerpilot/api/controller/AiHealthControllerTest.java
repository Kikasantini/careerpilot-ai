package ai.careerpilot.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ai.careerpilot.api.client.AiServiceClient;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class AiHealthControllerTest {

  @Test
  void shouldReturn503WhenAiServiceIsUnavailable() {
    // Simulate the Python service being unavailable.
    AiServiceClient client = mock(AiServiceClient.class);
    when(client.checkHealth()).thenReturn(Map.of("status", "unavailable"));

    AiHealthController controller = new AiHealthController(client);

    ResponseEntity<Map<String, String>> response = controller.checkHealth();

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    assertEquals("unavailable", response.getBody().get("status"));
  }

  @Test
  void shouldReturn200WhenAiServiceIsHealthy() {
    // Simulate a successful response from the Python AI service.
    AiServiceClient client = mock(AiServiceClient.class);
    when(client.checkHealth()).thenReturn(Map.of("status", "ok"));

    AiHealthController controller = new AiHealthController(client);

    ResponseEntity<Map<String, String>> response = controller.checkHealth();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("ok", response.getBody().get("status"));
  }
}
