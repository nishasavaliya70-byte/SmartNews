import uuid
from typing import Optional
from fastapi import APIRouter, Depends, HTTPException, Query, status
from sqlalchemy.ext.asyncio import AsyncSession
from app.core.database import get_db
from app.services.news_service import NewsService
from app.schemas.news import NewsArticleResponse, NewsListResponse

router = APIRouter(prefix="/news", tags=["News"])


@router.get("", response_model=NewsListResponse)
async def get_news_feed(
    category: Optional[str] = Query(None, description="Category filter (e.g. technology, business, science)"),
    page: int = Query(1, ge=1, description="Page number"),
    size: int = Query(10, ge=1, le=50, description="Page size"),
    db: AsyncSession = Depends(get_db)
):
    items, total = await NewsService.get_news_feed(
        db, category_slug=category, page=page, size=size
    )
    return NewsListResponse(
        items=items,
        total=total,
        page=page,
        size=size
    )


@router.get("/latest", response_model=NewsListResponse)
async def get_latest_news(
    limit: int = Query(10, ge=1, le=30),
    db: AsyncSession = Depends(get_db)
):
    items, total = await NewsService.get_news_feed(db, page=1, size=limit)
    return NewsListResponse(
        items=items,
        total=total,
        page=1,
        size=limit
    )


@router.get("/trending", response_model=NewsListResponse)
async def get_trending_news(
    limit: int = Query(5, ge=1, le=20),
    db: AsyncSession = Depends(get_db)
):
    items, total = await NewsService.get_news_feed(db, page=1, size=limit)
    return NewsListResponse(
        items=items,
        total=total,
        page=1,
        size=limit
    )


@router.get("/search", response_model=NewsListResponse)
async def search_news(
    q: str = Query(..., min_length=1, description="Search keyword or phrase"),
    category: Optional[str] = Query(None),
    page: int = Query(1, ge=1),
    size: int = Query(10, ge=1, le=50),
    db: AsyncSession = Depends(get_db)
):
    items, total = await NewsService.search_news(
        db, query=q, category_slug=category, page=page, size=size
    )
    return NewsListResponse(
        items=items,
        total=total,
        page=page,
        size=size
    )


@router.get("/category/{category_slug}", response_model=NewsListResponse)
async def get_news_by_category(
    category_slug: str,
    page: int = Query(1, ge=1),
    size: int = Query(10, ge=1, le=50),
    db: AsyncSession = Depends(get_db)
):
    items, total = await NewsService.get_news_feed(
        db, category_slug=category_slug, page=page, size=size
    )
    return NewsListResponse(
        items=items,
        total=total,
        page=page,
        size=size
    )


@router.get("/{news_id}", response_model=NewsArticleResponse)
async def get_news_detail(
    news_id: uuid.UUID,
    db: AsyncSession = Depends(get_db)
):
    article = await NewsService.get_article_detail(db, news_id=news_id)
    if not article:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"News article with ID '{news_id}' not found."
        )
    return article
