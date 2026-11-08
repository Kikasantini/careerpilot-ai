from fastapi.testclient import TestClient

from app.main import app

# TestClient allows HTTP requests without starting a real server.
client = TestClient(app)


def test_health_check():
    response = client.get("/health")

    assert response.status_code == 200
    assert response.json() == {"status": "ok"}
