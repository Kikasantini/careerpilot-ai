package ai.careerpilot.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ai.careerpilot.api.dto.JobAnalysisRequest;
import ai.careerpilot.api.dto.JobAnalysisResponse;
import org.junit.jupiter.api.Test;

class JobAnalysisServiceTest {

  private final JobAnalysisService service = new JobAnalysisService();

  @Test
  void shouldProcessJobDescription() {
    JobAnalysisRequest request = new JobAnalysisRequest(null, "Java developer position");

    JobAnalysisResponse result = service.analyzeJob(request);

    assertEquals("Job description received for analysis.", result.message());
    assertEquals("description", result.inputSource());
  }

  @Test
  void shouldProcessJobUrl() {
    JobAnalysisRequest request = new JobAnalysisRequest("https://example.com/jobs/123", null);

    JobAnalysisResponse result = service.analyzeJob(request);

    assertEquals("Job URL received for analysis.", result.message());
    assertEquals("url", result.inputSource());
  }

  @Test
  void shouldPrioritizeDescriptionWhenBothAreProvided() {
    JobAnalysisRequest request =
        new JobAnalysisRequest("https://example.com/jobs/123", "Java developer position");

    JobAnalysisResponse result = service.analyzeJob(request);

    assertEquals("Job description received for analysis.", result.message());
    assertEquals("description", result.inputSource());
  }
}
