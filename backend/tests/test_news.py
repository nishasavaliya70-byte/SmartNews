import pytest


@pytest.mark.asyncio
async def test_news_feed_endpoint(client):
    response = await client.get("/api/news?page=1&size=5")
    assert response.status_code == 200
    data = response.json()
    assert "items" in data
    assert "total" in data
    assert len(data["items"]) <= 5


@pytest.mark.asyncio
async def test_news_category_filter(client):
    response = await client.get("/api/news?category=technology&page=1&size=3")
    assert response.status_code == 200
    data = response.json()
    assert "items" in data


@pytest.mark.asyncio
async def test_news_search(client):
    response = await client.get("/api/news/search?q=quantum")
    assert response.status_code == 200
    data = response.json()
    assert "items" in data
