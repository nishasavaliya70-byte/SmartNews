import uuid
from typing import List, Optional
from sqlalchemy import select, and_, desc
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import selectinload
from app.models.bookmark import Bookmark
from app.models.news import NewsArticle
from app.schemas.bookmark import BookmarkResponse
from app.services.news_service import NewsService


class BookmarkService:

    @staticmethod
    async def add_bookmark(db: AsyncSession, user_id: uuid.UUID, news_id: uuid.UUID) -> Optional[Bookmark]:
        # Check if already bookmarked
        stmt = select(Bookmark).where(
            and_(Bookmark.user_id == user_id, Bookmark.news_id == news_id)
        )
        res = await db.execute(stmt)
        existing = res.scalars().first()
        if existing:
            return existing

        bookmark = Bookmark(user_id=user_id, news_id=news_id)
        db.add(bookmark)
        await db.commit()
        await db.refresh(bookmark)
        return bookmark

    @staticmethod
    async def remove_bookmark(db: AsyncSession, user_id: uuid.UUID, news_id: uuid.UUID) -> bool:
        stmt = select(Bookmark).where(
            and_(Bookmark.user_id == user_id, Bookmark.news_id == news_id)
        )
        res = await db.execute(stmt)
        bookmark = res.scalars().first()
        if not bookmark:
            return False

        await db.delete(bookmark)
        await db.commit()
        return True

    @staticmethod
    async def get_user_bookmarks(db: AsyncSession, user_id: uuid.UUID) -> List[BookmarkResponse]:
        stmt = (
            select(Bookmark)
            .options(
                selectinload(Bookmark.article).selectinload(NewsArticle.category)
            )
            .where(Bookmark.user_id == user_id)
            .order_by(desc(Bookmark.created_at))
        )
        res = await db.execute(stmt)
        bookmarks = list(res.scalars().all())

        responses = []
        for b in bookmarks:
            if b.article:
                art_resp = NewsService._to_response(b.article)
                art_resp.is_bookmarked = True
                responses.append(
                    BookmarkResponse(
                        id=b.id,
                        user_id=b.user_id,
                        news_id=b.news_id,
                        created_at=b.created_at,
                        article=art_resp
                    )
                )
        return responses
