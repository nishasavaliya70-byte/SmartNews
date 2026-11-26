import uuid
from typing import List
from fastapi import APIRouter, Depends, Query, status
from sqlalchemy.ext.asyncio import AsyncSession
from app.core.database import get_db
from app.api.deps import get_current_active_user
from app.models.user import User
from app.schemas.news import NewsArticleResponse
from app.services.history_service import HistoryService
from pydantic import BaseModel

router = APIRouter(prefix="/history", tags=["Reading History"])


class RecordHistoryRequest(BaseModel):
    news_id: uuid.UUID


@router.get("", response_model=List[NewsArticleResponse])
async def get_reading_history(
    limit: int = Query(20, ge=1, le=50),
    current_user: User = Depends(get_current_active_user),
    db: AsyncSession = Depends(get_db)
):
    return await HistoryService.get_user_history(db, user_id=current_user.id, limit=limit)


@router.post("", status_code=status.HTTP_201_CREATED)
async def record_reading_history(
    request: RecordHistoryRequest,
    current_user: User = Depends(get_current_active_user),
    db: AsyncSession = Depends(get_db)
):
    await HistoryService.record_view(db, user_id=current_user.id, news_id=request.news_id)
    return {"status": "success", "message": "History recorded."}


@router.delete("", status_code=status.HTTP_200_OK)
async def clear_reading_history(
    current_user: User = Depends(get_current_active_user),
    db: AsyncSession = Depends(get_db)
):
    count = await HistoryService.clear_user_history(db, user_id=current_user.id)
    return {"status": "success", "cleared_count": count}
