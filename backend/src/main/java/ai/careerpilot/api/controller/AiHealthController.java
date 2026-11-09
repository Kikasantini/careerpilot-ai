package ai.careerpilot.api.controller;

import ai.careerpilot.api.client.AiServiceClient;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiHealthController {

  private final AiServiceClient aiServiceClient;

  public AiHealthController(AiServiceClient aiServiceClient) {
    this.aiServiceClient = aiServiceClient;
  }

  @GetMapping("/health")
  public ResponseEntity<Map<String, String>> checkHealth() {
    // Return HTTP 503 when the Python AI service is unavailable.
    Map<String, String> health = aiServiceClient.checkHealth();

    if ("unavailable".equals(health.get("status"))) {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(health);
    }

    return ResponseEntity.ok(health);
  }
}
