package ai.careerpilot.api.dto;

import jakarta.validation.constraints.AssertTrue;
import java.net.URI;
import java.net.URISyntaxException;

public record JobAnalysisRequest(String jobUrl, String jobDescription) {

  // At least one source must be provided to analyze a job posting.
  @AssertTrue(message = "Provide a job URL or a job description.")
  public boolean isJobInputProvided() {
    return (jobUrl != null && !jobUrl.isBlank())
        || (jobDescription != null && !jobDescription.isBlank());
  }

  @AssertTrue(message = "Job URL must be a valid HTTP or HTTPS URL.")
  public boolean isJobUrlValid() {
    // An empty URL is allowed when a job description is provided.
    if (jobUrl == null || jobUrl.isBlank()) {
      return true;
    }

    try {
      URI uri = new URI(jobUrl);

      // Only accept absolute HTTP(S) URLs with a valid host.
      String scheme = uri.getScheme();

      return ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
          && uri.getHost() != null
          && !uri.getHost().isBlank();

    } catch (URISyntaxException e) {
      return false;
    }
  }
}
