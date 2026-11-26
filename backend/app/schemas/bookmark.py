import uuid
from datetime import datetime
from pydantic import BaseModel
from app.schemas.news import NewsArticleResponse


class BookmarkCreate(BaseModel):
    news_id: uuid.UUID


class BookmarkResponse(BaseModel):
    id: uuid.UUID
    user_id: uuid.UUID
    news_id: uuid.UUID
    created_at: datetime
    article: NewsArticleResponse

    class Config:
        from_attributes = True
