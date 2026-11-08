package ai.careerpilot.api.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

class JobAnalysisRequestTest {

  @Test
  void shouldAcceptJobUrlOnly() {
    JobAnalysisRequest request = new JobAnalysisRequest("https://example.com/jobs/123", null);

    assertTrue(request.isJobInputProvided());
  }

  @Test
  void shouldAcceptJobDescriptionOnly() {
    JobAnalysisRequest request = new JobAnalysisRequest(null, "Java developer position");

    assertTrue(request.isJobInputProvided());
  }

  @Test
  void shouldAcceptBothInputs() {
    JobAnalysisRequest request =
        new JobAnalysisRequest("https://example.com/jobs/123", "Java developer position");

    assertTrue(request.isJobInputProvided());
  }

  @Test
  void shouldRejectMissingInputs() {
    JobAnalysisRequest request = new JobAnalysisRequest(null, null);

    assertFalse(request.isJobInputProvided());
  }

  @Test
  void shouldRejectBlankInputs() {
    JobAnalysisRequest request = new JobAnalysisRequest("   ", "\n\t");

    assertFalse(request.isJobInputProvided());
  }

  @Test
  void shouldFailBeanValidationWhenBothInputsAreMissing() {
    // Use the actual Jakarta Bean Validation engine to evaluate @AssertTrue.
    try (var factory = Validation.buildDefaultValidatorFactory()) {
      Validator validator = factory.getValidator();

      JobAnalysisRequest request = new JobAnalysisRequest(null, null);

      var violations = validator.validate(request);

      assertFalse(violations.isEmpty());
    }
  }

  @Test
  void shouldAcceptValidHttpAndHttpsUrls() {
    JobAnalysisRequest httpRequest = new JobAnalysisRequest("http://example.com/jobs/123", null);

    JobAnalysisRequest httpsRequest = new JobAnalysisRequest("https://example.com/jobs/123", null);

    assertTrue(httpRequest.isJobUrlValid());
    assertTrue(httpsRequest.isJobUrlValid());
  }

  @Test
  void shouldRejectInvalidJobUrls() {
    JobAnalysisRequest missingScheme = new JobAnalysisRequest("example.com/jobs/123", null);

    JobAnalysisRequest invalidScheme = new JobAnalysisRequest("ftp://example.com/jobs/123", null);

    JobAnalysisRequest missingHost = new JobAnalysisRequest("https:///jobs/123", null);

    assertFalse(missingScheme.isJobUrlValid());
    assertFalse(invalidScheme.isJobUrlValid());
    assertFalse(missingHost.isJobUrlValid());
  }
}
