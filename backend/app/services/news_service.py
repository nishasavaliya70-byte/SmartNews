import uuid
from typing import List, Dict, Any, Optional, Tuple
from sqlalchemy.ext.asyncio import AsyncSession
from app.models.news import NewsArticle
from app.repositories.news_repository import NewsRepository
from app.repositories.category_repository import CategoryRepository
from app.services.gnews_service import GNewsService
from app.schemas.news import NewsArticleResponse


class NewsService:

    @classmethod
    async def sync_external_news(
        cls,
        db: AsyncSession,
        category_slug: str = "general",
        max_results: int = 10
    ) -> List[NewsArticle]:
        """Fetches from GNews, normalizes, links category, and deduplicates in PostgreSQL."""
        raw_articles = await GNewsService.fetch_top_headlines(
            category=category_slug, max_results=max_results
        )

        persisted: List[NewsArticle] = []
        for raw in raw_articles:
            cat_name = raw.pop("category_name", "General")
            category = await CategoryRepository.get_or_create(
                db, name=cat_name, slug=cat_name.lower()
            )

            raw["category_id"] = category.id
            article = await NewsRepository.create_or_ignore(db, article_data=raw)
            if article:
                persisted.append(article)
        return persisted

    @classmethod
    async def get_news_feed(
        cls,
        db: AsyncSession,
        category_slug: Optional[str] = None,
        page: int = 1,
        size: int = 10
    ) -> Tuple[List[NewsArticleResponse], int]:
        category_id = None
        if category_slug and category_slug.lower() not in ("all", "general"):
            category = await CategoryRepository.get_by_slug(db, slug=category_slug.lower())
            if category:
                category_id = category.id

        items, total = await NewsRepository.get_paginated(
            db, category_id=category_id, page=page, size=size
        )

        # If database is fresh or empty, trigger GNews synchronization
        if total == 0:
            await cls.sync_external_news(db, category_slug=category_slug or "general")
            items, total = await NewsRepository.get_paginated(
                db, category_id=category_id, page=page, size=size
            )

        responses = [cls._to_response(item) for item in items]
        return responses, total

    @classmethod
    async def search_news(
        cls,
        db: AsyncSession,
        query: str,
        category_slug: Optional[str] = None,
        page: int = 1,
        size: int = 10
    ) -> Tuple[List[NewsArticleResponse], int]:
        category_id = None
        if category_slug and category_slug.lower() != "all":
            category = await CategoryRepository.get_by_slug(db, slug=category_slug.lower())
            if category:
                category_id = category.id

        items, total = await NewsRepository.search(
            db, query_text=query, category_id=category_id, page=page, size=size
        )

        # If zero local results, search via GNews and persist
        if total == 0:
            external = await GNewsService.search_articles(query=query)
            for raw in external:
                cat_name = raw.pop("category_name", "General")
                cat = await CategoryRepository.get_or_create(db, name=cat_name, slug=cat_name.lower())
                raw["category_id"] = cat.id
                await NewsRepository.create_or_ignore(db, article_data=raw)

            items, total = await NewsRepository.search(
                db, query_text=query, category_id=category_id, page=page, size=size
            )

        responses = [cls._to_response(item) for item in items]
        return responses, total

    @classmethod
    async def get_article_detail(
        cls,
        db: AsyncSession,
        news_id: uuid.UUID
    ) -> Optional[NewsArticleResponse]:
        article = await NewsRepository.get_by_id(db, news_id=news_id)
        if not article:
            return None
        return cls._to_response(article)

    @staticmethod
    def _to_response(article: NewsArticle) -> NewsArticleResponse:
        cat_name = article.category.name if article.category else "General"
        return NewsArticleResponse(
            id=article.id,
            title=article.title,
            description=article.description,
            content=article.content,
            source_name=article.source_name,
            source_url=article.source_url,
            article_url=article.article_url,
            image_url=article.image_url,
            language=article.language,
            country=article.country,
            published_at=article.published_at,
            category_id=article.category_id,
            category_name=cat_name,
            is_bookmarked=False,
            ai_credibility_score=0.92,
            ai_category_prediction=cat_name,
            ai_summary_short=article.description[:120] + "...",
            created_at=article.created_at
        )
