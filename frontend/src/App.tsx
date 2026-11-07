import { useState } from 'react';
import './App.css';

function App() {
  const [jobDescription, setJobDescription] = useState('');
  const [jobUrl, setJobUrl] = useState('');

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
          onChange={(event) => setJobUrl(event.target.value)}
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

        <button type="button" disabled>
          Analyze Job
        </button>
      </section>
    </main>
  );
}

export default App;
