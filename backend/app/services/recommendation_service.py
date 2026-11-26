import uuid
from typing import List
from sqlalchemy.ext.asyncio import AsyncSession
from app.models.user import User
from app.services.news_service import NewsService
from app.services.history_service import HistoryService
from app.services.bookmark_service import BookmarkService
from app.ml.recommendation import RecommendationEngine
from app.schemas.news import NewsArticleResponse


class RecommendationService:

    @classmethod
    async def get_personalized_recommendations(
        cls,
        db: AsyncSession,
        user: User,
        limit: int = 10
    ) -> List[NewsArticleResponse]:
        # 1. Gather Candidate Articles
        candidates, _ = await NewsService.get_news_feed(db, page=1, size=40)

        # 2. Gather User Interests & History
        history = await HistoryService.get_user_history(db, user_id=user.id, limit=20)
        bookmarks = await BookmarkService.get_user_bookmarks(db, user_id=user.id)

        history_ids = [str(h.id) for h in history]
        bookmark_texts = [f"{b.article.title} {b.article.category_name}" for b in bookmarks]
        history_texts = [f"{h.title} {h.category_name}" for h in history]
        interest_texts = [i.category.name for i in user.interests if i.category]

        user_profile_text = " ".join(interest_texts + bookmark_texts + history_texts)
        if not user_profile_text.strip():
            user_profile_text = "Technology Science Business World Innovation"

        candidate_dicts = [c.model_dump() for c in candidates]

        # 3. Compute Content-Based Filtering via Cosine Similarity
        recommended = RecommendationEngine.recommend_articles(
            user_preference_text=user_profile_text,
            candidate_articles=candidate_dicts,
            exclude_article_ids=history_ids,
            top_n=limit
        )

        return [NewsArticleResponse(**r) for r in recommended]
