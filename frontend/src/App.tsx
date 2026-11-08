import { useState } from 'react';
import { analyzeJob } from './services/jobService';
import './App.css';

function App() {
  const [jobDescription, setJobDescription] = useState('');
  const [jobUrl, setJobUrl] = useState('');
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  async function handleAnalyzeJob() {
    setErrorMessage('');
    setSuccessMessage('');
    setIsLoading(true);

    try {
      // Use the description when both inputs are provided.
      const response = await analyzeJob({
        jobUrl: jobDescription.trim() ? '' : jobUrl.trim(),
        jobDescription: jobDescription.trim(),
      });

      setSuccessMessage(response);
    } catch (error) {
      // Display a friendly message when the API request fails.
      setErrorMessage(error instanceof Error ? error.message : 'An unexpected error occurred.');
    } finally {
      // Always stop loading, whether the request succeeds or fails.
      setIsLoading(false);
    }
  }

  return (
    <main>
      <h1>CareerPilot AI</h1>
      <p>Your AI-powered career application assistant.</p>

      <section className="job-form">
        <h2>Job Details</h2>

        <label htmlFor="job-url">Job posting URL</label>
        <input
          id="job-url"
          type="url"
          value={jobUrl}

          onChange={(event) => {
            setJobUrl(event.target.value);
            setErrorMessage(null);
          }}

          placeholder="https://company.com/careers/job"
        />

        <p>Or paste the job description:</p>

        <label htmlFor="job-description">Job description</label>
        <textarea
          id="job-description"
          value={jobDescription}
          onChange={(event) => setJobDescription(event.target.value)}
          placeholder="Paste the full job description here..."
          rows={10}
        />

        <button
          type="button"
          onClick={handleAnalyzeJob}
          disabled={(!jobUrl.trim() && !jobDescription.trim()) || isLoading}
        >
          {isLoading ? 'Analyzing...' : 'Analyze Job'}
        </button>

        {successMessage && (
          <p role="status" className="success-message">
            {successMessage}
          </p>
        )}

        {errorMessage && (
          <p className="error-message" role="alert">
            {errorMessage}
          </p>
        )}
      </section>
    </main>
  );
}

export default App;
