from typing import List, Dict, Any
from fastapi import APIRouter, Depends, Query, status
from sqlalchemy.ext.asyncio import AsyncSession
from app.core.database import get_db
from app.api.deps import get_current_active_user
from app.models.user import User
from app.schemas.news import NewsArticleResponse
from app.services.summary_service import SummaryService, SummarizeRequest, SummarizeResponse
from app.services.recommendation_service import RecommendationService
from app.services.trending_service import TrendingService

# Summaries Router
summaries_router = APIRouter(prefix="/ai", tags=["AI Summarization & Recommendations"])


@summaries_router.post("/summarize", response_model=SummarizeResponse, status_code=status.HTTP_200_OK)
async def summarize_news_article(
    request: SummarizeRequest,
    db: AsyncSession = Depends(get_db)
):
    return await SummaryService.summarize_news(db, request)


@summaries_router.get("/recommendations", response_model=List[NewsArticleResponse])
async def get_personalized_recommendations(
    limit: int = Query(10, ge=1, le=30),
    current_user: User = Depends(get_current_active_user),
    db: AsyncSession = Depends(get_db)
):
    """Module 3: Content-based news recommendation using TF-IDF & Cosine Similarity."""
    return await RecommendationService.get_personalized_recommendations(
        db, user=current_user, limit=limit
    )


# Trending Router
trending_router = APIRouter(prefix="/trending", tags=["Trending News Analysis"])


@trending_router.get("/topics", status_code=status.HTTP_200_OK)
async def get_trending_topics(
    time_window: str = Query("24h"),
    db: AsyncSession = Depends(get_db)
):
    """Module 5: Emerging topic identification and frequency growth velocity."""
    insights = await TrendingService.get_trending_insights(db, time_window=time_window)
    return insights["trending_topics"]


@trending_router.get("/categories", status_code=status.HTTP_200_OK)
async def get_trending_categories(
    time_window: str = Query("24h"),
    db: AsyncSession = Depends(get_db)
):
    insights = await TrendingService.get_trending_insights(db, time_window=time_window)
    return insights["trending_categories"]
