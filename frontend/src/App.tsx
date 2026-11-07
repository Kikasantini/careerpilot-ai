import { useState } from 'react';
import './App.css';

function App() {
  const [jobDescription, setJobDescription] = useState('');
  const [jobUrl, setJobUrl] = useState('');
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const inputSource = jobDescription.trim() ? 'description' : 'url';

  function handleAnalyzeJob() {
    setErrorMessage(null);

    if (inputSource === 'description') {
      console.log('Job description:', jobDescription.trim());
      return;
    }

    try {
      const url = new URL(jobUrl.trim());

      if (!['http:', 'https:'].includes(url.protocol)) {
        throw new Error('Unsupported URL protocol');
      }

      console.log('Job URL:', url.href);
    } catch {
      setErrorMessage('Please enter a valid job posting URL.');
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
          disabled={!jobUrl.trim() && !jobDescription.trim()}
        >
          Analyze Job
        </button>

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
