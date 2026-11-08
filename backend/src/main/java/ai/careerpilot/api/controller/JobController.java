package ai.careerpilot.api.controller;

import ai.careerpilot.api.dto.JobAnalysisRequest;
import ai.careerpilot.api.dto.JobAnalysisResponse;
import ai.careerpilot.api.service.JobAnalysisService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// @RestController: Combines @Controller and @ResponseBody
// @PostMapping: Maps a Java method to an HTTP POST request.
// @RequestBody: Converts the incoming JSON request body into a Java object.
@RestController
public class JobController {

  // Final ensures the service reference cannot be reassigned after initialization.
  private final JobAnalysisService jobAnalysisService;

  // Spring injects the service through the constructor.
  public JobController(JobAnalysisService jobAnalysisService) {
    this.jobAnalysisService = jobAnalysisService;
  }

  @GetMapping("/api/jobs/hello")
  public String hello() {
    return "CareerPilot Job API is running!";
  }

  @PostMapping("/api/jobs/analyze")
  public JobAnalysisResponse analyzeJob(@Valid @RequestBody JobAnalysisRequest request) {
    // Delegate processing to the service and return a structured response.
    return jobAnalysisService.analyzeJob(request);
  }
}
