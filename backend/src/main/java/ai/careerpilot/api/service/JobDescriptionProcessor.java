package ai.careerpilot.api.service;

import org.springframework.stereotype.Component;

@Component
public class JobDescriptionProcessor {

  public String normalize(String description) {
    // Remove leading and trailing whitespace.
    return description.strip();
  }
}
