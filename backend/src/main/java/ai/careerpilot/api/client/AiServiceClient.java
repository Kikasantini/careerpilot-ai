package ai.careerpilot.api.client;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class AiServiceClient {

  private final RestClient restClient;

  public AiServiceClient(
      RestClient.Builder restClientBuilder, @Value("${ai.service.base-url}") String baseUrl) {

    this.restClient = restClientBuilder.baseUrl(baseUrl).build();
  }

  public Map<String, String> checkHealth() {
    try {
      // Request the health status from the Python AI service.
      return restClient
          .get()
          .uri("/health")
          .retrieve()
          .body(new org.springframework.core.ParameterizedTypeReference<>() {});
    } catch (HttpServerErrorException exception) {
      // Handle HTTP 503 without hiding other server errors.
      if (exception.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
        return Map.of("status", "unavailable");
      }

      throw exception;
    } catch (ResourceAccessException exception) {
      // Handle connection failures when the Python service cannot be reached.
      return Map.of("status", "unavailable");
    }
  }
}
