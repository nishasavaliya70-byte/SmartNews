import uuid
from datetime import datetime
from typing import Optional, List
from pydantic import BaseModel


class PredictionRequest(BaseModel):
    headline: Optional[str] = None
    content: Optional[str] = None
    article_url: Optional[str] = None
    news_id: Optional[uuid.UUID] = None


class PredictionResponse(BaseModel):
    id: Optional[uuid.UUID] = None
    news_id: Optional[uuid.UUID] = None
    predicted_category: str
    classification_label: str
    confidence_score: float
    credibility_score: float
    model_version: str
    explanation: Optional[str] = None
    suspicious_language_signals: List[str] = []
    key_takeaways: List[str] = []
    important_keywords: List[str] = []
    verification_tips: List[str] = []
    created_at: Optional[datetime] = None

    class Config:
        from_attributes = True
