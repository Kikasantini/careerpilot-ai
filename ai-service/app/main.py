from fastapi import FastAPI

# Create the FastAPI application instance.
app = FastAPI(title="CareerPilot AI Service")


@app.get("/health")
def health_check():
    # Return a simple response to confirm the service is running.
    return {"status": "ok"}
