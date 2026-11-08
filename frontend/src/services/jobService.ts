export interface JobAnalysisRequest {
  jobUrl: string;
  jobDescription: string;
}

export interface JobAnalysisResponse {
  message: string;
  inputSource: 'url' | 'description';
}

export async function analyzeJob(request: JobAnalysisRequest): Promise<JobAnalysisResponse> {
  let response: Response;

  try {
    // Send the job information to the Spring Boot API.
    response = await fetch('/api/jobs/analyze', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });
  } catch {
    // Network failures can occur before an HTTP response is received.
    throw new Error('Network error: Unable to connect to the server.');
  }

  if (!response.ok) {
    // Keep HTTP status codes visible to help identify different errors.
    if (response.status === 400) {
      throw new Error('HTTP 400 Bad Request: Please enter a valid job URL or description.');
    }

    if (response.status === 502) {
      throw new Error('HTTP 502 Bad Gateway: Unable to reach the backend server.');
    }

    if (response.status === 503) {
      throw new Error('HTTP 503 Service Unavailable: The server is temporarily unavailable.');
    }

    throw new Error(`HTTP ${response.status}: Something went wrong. Please try again.`);
  }

  // Parse the JSON response returned by the Spring Boot API.
  return (await response.json()) as JobAnalysisResponse;
}
