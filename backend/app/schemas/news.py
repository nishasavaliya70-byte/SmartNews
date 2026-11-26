import uuid
from datetime import datetime
from typing import Optional, List
from pydantic import BaseModel, HttpUrl


class NewsArticleBase(BaseModel):
    title: str
    description: str
    content: Optional[str] = None
    source_name: str
    source_url: Optional[str] = None
    article_url: str
    image_url: Optional[str] = None
    language: str = "en"
    country: str = "us"
    published_at: datetime


class NewsArticleCreate(NewsArticleBase):
    category_id: Optional[uuid.UUID] = None


class NewsArticleResponse(NewsArticleBase):
    id: uuid.UUID
    category_id: Optional[uuid.UUID] = None
    category_name: Optional[str] = "General"
    is_bookmarked: bool = False
    ai_credibility_score: Optional[float] = None
    ai_category_prediction: Optional[str] = None
    ai_summary_short: Optional[str] = None
    created_at: datetime

    class Config:
        from_attributes = True


class NewsListResponse(BaseModel):
    items: List[NewsArticleResponse]
    total: int
    page: int
    size: int
