import uuid
from typing import List, Optional, Tuple
from sqlalchemy import select, func, or_, desc
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import selectinload
from app.models.news import NewsArticle


class NewsRepository:

    @staticmethod
    async def get_by_id(db: AsyncSession, news_id: uuid.UUID) -> Optional[NewsArticle]:
        stmt = (
            select(NewsArticle)
            .options(selectinload(NewsArticle.category))
            .where(NewsArticle.id == news_id)
        )
        result = await db.execute(stmt)
        return result.scalars().first()

    @staticmethod
    async def get_by_url(db: AsyncSession, article_url: str) -> Optional[NewsArticle]:
        stmt = select(NewsArticle).where(NewsArticle.article_url == article_url)
        result = await db.execute(stmt)
        return result.scalars().first()

    @staticmethod
    async def create_or_ignore(db: AsyncSession, article_data: dict) -> Optional[NewsArticle]:
        """Prevents duplicate article ingestion by checking canonical article_url."""
        url = article_data.get("article_url")
        if not url:
            return None

        existing = await NewsRepository.get_by_url(db, article_url=url)
        if existing:
            return existing

        article = NewsArticle(**article_data)
        db.add(article)
        try:
            await db.commit()
            await db.refresh(article)
            return article
        except Exception:
            await db.rollback()
            return await NewsRepository.get_by_url(db, article_url=url)

    @staticmethod
    async def get_paginated(
        db: AsyncSession,
        category_id: Optional[uuid.UUID] = None,
        page: int = 1,
        size: int = 10
    ) -> Tuple[List[NewsArticle], int]:
        offset = (page - 1) * size

        # Query count
        count_query = select(func.count(NewsArticle.id))
        if category_id:
            count_query = count_query.where(NewsArticle.category_id == category_id)
        total_res = await db.execute(count_query)
        total = total_res.scalar_one()

        # Query items
        query = (
            select(NewsArticle)
            .options(selectinload(NewsArticle.category))
            .order_by(desc(NewsArticle.published_at))
            .offset(offset)
            .limit(size)
        )
        if category_id:
            query = query.where(NewsArticle.category_id == category_id)

        items_res = await db.execute(query)
        items = list(items_res.scalars().all())
        return items, total

    @staticmethod
    async def search(
        db: AsyncSession,
        query_text: str,
        category_id: Optional[uuid.UUID] = None,
        page: int = 1,
        size: int = 10
    ) -> Tuple[List[NewsArticle], int]:
        offset = (page - 1) * size
        search_filter = or_(
            NewsArticle.title.ilike(f"%{query_text}%"),
            NewsArticle.description.ilike(f"%{query_text}%"),
            NewsArticle.source_name.ilike(f"%{query_text}%")
        )

        count_query = select(func.count(NewsArticle.id)).where(search_filter)
        if category_id:
            count_query = count_query.where(NewsArticle.category_id == category_id)
        total_res = await db.execute(count_query)
        total = total_res.scalar_one()

        query = (
            select(NewsArticle)
            .options(selectinload(NewsArticle.category))
            .where(search_filter)
            .order_by(desc(NewsArticle.published_at))
            .offset(offset)
            .limit(size)
        )
        if category_id:
            query = query.where(NewsArticle.category_id == category_id)

        items_res = await db.execute(query)
        items = list(items_res.scalars().all())
        return items, total
