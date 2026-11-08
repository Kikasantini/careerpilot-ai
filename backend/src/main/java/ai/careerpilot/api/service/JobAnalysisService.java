package ai.careerpilot.api.service;

import ai.careerpilot.api.dto.JobAnalysisRequest;
import ai.careerpilot.api.dto.JobAnalysisResponse;
import org.springframework.stereotype.Service;

@Service
public class JobAnalysisService {

  public JobAnalysisResponse analyzeJob(JobAnalysisRequest request) {
    // Prioritize the pasted description when both inputs are provided.
    if (request.jobDescription() != null && !request.jobDescription().isBlank()) {
      return new JobAnalysisResponse("Job description received for analysis.", "description");
    }

    return new JobAnalysisResponse("Job URL received for analysis.", "url");
  }
}
