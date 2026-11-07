package ai.careerpilot.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc; // tool that allows to test HTTP endpoints without a real server on port 8080
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class JobControllerTest {

    @Test
    void helloShouldReturnSuccessMessage() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new JobController()) // creates a test environment for the controller, no need to start the entire application
                .build();

        mockMvc.perform(get("/api/jobs/hello")) // "perform" simulates the HTTP GET request
                .andExpect(status().isOk()) // verifies that the response status is HTTP 200 OK
                .andExpect(content().string("CareerPilot Job API is running!"));
    }
}
