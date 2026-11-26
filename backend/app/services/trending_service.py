from typing import Dict, Any
from sqlalchemy.ext.asyncio import AsyncSession
from app.services.news_service import NewsService
from app.ml.trending_analysis import TrendingAnalysisEngine


class TrendingService:

    @classmethod
    async def get_trending_insights(
        cls,
        db: AsyncSession,
        time_window: str = "24h"
    ) -> Dict[str, Any]:
        # Fetch recent news articles
        recent_articles, _ = await NewsService.get_news_feed(db, page=1, size=50)
        article_dicts = [a.model_dump() for a in recent_articles]

        return TrendingAnalysisEngine.analyze_emerging_topics(
            articles=article_dicts,
            time_window=time_window
        )
