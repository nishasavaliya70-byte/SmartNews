import uuid
from typing import List
from sqlalchemy import select, desc, delete
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import selectinload
from app.models.reading_history import ReadingHistory
from app.models.news import NewsArticle
from app.schemas.news import NewsArticleResponse
from app.services.news_service import NewsService


class HistoryService:

    @staticmethod
    async def record_view(db: AsyncSession, user_id: uuid.UUID, news_id: uuid.UUID) -> ReadingHistory:
        history = ReadingHistory(user_id=user_id, news_id=news_id)
        db.add(history)
        await db.commit()
        await db.refresh(history)
        return history

    @staticmethod
    async def get_user_history(db: AsyncSession, user_id: uuid.UUID, limit: int = 20) -> List[NewsArticleResponse]:
        stmt = (
            select(ReadingHistory)
            .options(
                selectinload(ReadingHistory.article).selectinload(NewsArticle.category)
            )
            .where(ReadingHistory.user_id == user_id)
            .order_by(desc(ReadingHistory.viewed_at))
            .limit(limit)
        )
        res = await db.execute(stmt)
        records = list(res.scalars().all())

        articles: List[NewsArticleResponse] = []
        for r in records:
            if r.article:
                articles.append(NewsService._to_response(r.article))
        return articles

    @staticmethod
    async def clear_user_history(db: AsyncSession, user_id: uuid.UUID) -> int:
        stmt = delete(ReadingHistory).where(ReadingHistory.user_id == user_id)
        result = await db.execute(stmt)
        await db.commit()
        return result.rowcount
