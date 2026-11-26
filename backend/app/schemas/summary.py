import uuid
from datetime import datetime
from pydantic import BaseModel


class SummaryResponse(BaseModel):
    id: uuid.UUID
    news_id: uuid.UUID
    summary: str
    summary_type: str
    model_version: str
    created_at: datetime

    class Config:
        from_attributes = True
