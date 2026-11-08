package ai.careerpilot.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class JobControllerTest {
  private MockMvc mockMvc;

  // MockMvc is a tool that allows to test HTTP endpoints without a real server on port 8080.
  // StandaloneSetup creates a test environment without starting the full Spring Boot application.

  @BeforeEach
  void setUp() {
    // Initialize MockMvc before each test without starting the full Spring application.
    mockMvc = MockMvcBuilders.standaloneSetup(new JobController()).build();
  }

  @Test
  void helloShouldReturnSuccessMessage() throws Exception {
    mockMvc
        .perform(get("/api/jobs/hello")) // "perform" simulates the HTTP GET request
        .andExpect(status().isOk()) // verifies that the response status is HTTP 200 OK
        .andExpect(content().string("CareerPilot Job API is running!"));
  }

  @Test
  void analyzeJobShouldAcceptValidDescription() throws Exception {
    // Simulate an HTTP POST request with a JSON body.
    String requestBody =
        """
        {
          "jobUrl": null,
          "jobDescription": "Java developer position"
        }
        """;

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/jobs/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().string("Job analysis request received!"));
  }

  @Test
  void analyzeJobShouldRejectMissingInputs() throws Exception {
    String requestBody =
        """
        {
          "jobUrl": null,
          "jobDescription": null
        }
        """;

    // A request without a URL or description should return HTTP 400.
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/jobs/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isBadRequest());
  }

  @Test
  void analyzeJobShouldRejectInvalidUrl() throws Exception {
    String requestBody =
        """
        {
          "jobUrl": "not-a-valid-url",
          "jobDescription": null
        }
        """;

    // Job URLs must use a valid HTTP or HTTPS address.
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/jobs/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isBadRequest());
  }
}
