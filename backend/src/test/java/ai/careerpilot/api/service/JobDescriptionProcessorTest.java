package ai.careerpilot.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class JobDescriptionProcessorTest {

  private final JobDescriptionProcessor processor = new JobDescriptionProcessor();

  @Test
  void shouldRemoveLeadingAndTrailingWhitespace() {
    String description = "   Java developer position   ";

    String result = processor.normalize(description);

    assertEquals("Java developer position", result);
  }

  @Test
  void shouldPreserveInternalWhitespace() {
    String description = "Java developer\n\nSpring Boot experience";

    String result = processor.normalize(description);

    assertEquals("Java developer\n\nSpring Boot experience", result);
  }

  @Test
  void shouldPreserveOriginalCapitalization() {
    String description = "  Java, Spring Boot, PostgreSQL  ";

    String result = processor.normalize(description);

    assertEquals("Java, Spring Boot, PostgreSQL", result);
  }
}
