package ai.careerpilot.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Combines @Controller and @ResponseBody
public class JobController {
  @GetMapping("/api/jobs/hello")
  public String hello() {
    return "CareerPilot Job API is running!";
  }
}
