package ai.careerpilot.api.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class AiServiceClientTest {

  @Test
  void shouldReturnAiServiceHealthStatus() {
    RestClient.Builder builder = RestClient.builder();

    // Intercept HTTP requests without contacting the real Python service.
    MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

    AiServiceClient client = new AiServiceClient(builder, "http://localhost:8000");

    server
        .expect(
            request -> assertEquals("http://localhost:8000/health", request.getURI().toString()))
        .andRespond(
            org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess(
                "{\"status\":\"ok\"}", org.springframework.http.MediaType.APPLICATION_JSON));

    Map<String, String> response = client.checkHealth();

    assertEquals("ok", response.get("status"));
    server.verify();
  }

  @Test
  void shouldHandleAiServiceUnavailable() {
    RestClient.Builder builder = RestClient.builder();

    MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

    AiServiceClient client = new AiServiceClient(builder, "http://localhost:8000");

    // Simulate the AI service returning HTTP 503.
    server
        .expect(
            request -> assertEquals("http://localhost:8000/health", request.getURI().toString()))
        .andRespond(
            org.springframework.test.web.client.response.MockRestResponseCreators.withStatus(
                org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE));

    Map<String, String> response = client.checkHealth();

    assertEquals("unavailable", response.get("status"));
    server.verify();
  }

  @Test
  void shouldHandleAiServiceConnectionFailure() {
    RestClient.Builder builder = RestClient.builder();

    MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

    AiServiceClient client = new AiServiceClient(builder, "http://localhost:8000");

    // Simulate a connection failure before an HTTP response is received.
    server
        .expect(
            request -> assertEquals("http://localhost:8000/health", request.getURI().toString()))
        .andRespond(request -> {
          throw new IOException("Connection refused");
        });

    Map<String, String> response = client.checkHealth();

    assertEquals("unavailable", response.get("status"));
    server.verify();
  }
}
