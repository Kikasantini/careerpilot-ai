package ai.careerpilot.api.controller;

import ai.careerpilot.api.dto.JobAnalysisRequest;
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

  @GetMapping("/api/jobs/hello")
  public String hello() {
    return "CareerPilot Job API is running!";
  }

  @PostMapping("/api/jobs/analyze")
  public String analyzeJob(@Valid @RequestBody JobAnalysisRequest request) {
    // Temporary response to confirm that the API receives the job data.
    return "Job analysis request received!";
  }
}
