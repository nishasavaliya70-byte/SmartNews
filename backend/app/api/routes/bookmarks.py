import uuid
from typing import List
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession
from app.core.database import get_db
from app.api.deps import get_current_active_user
from app.models.user import User
from app.schemas.bookmark import BookmarkCreate, BookmarkResponse
from app.services.bookmark_service import BookmarkService

router = APIRouter(prefix="/bookmarks", tags=["Bookmarks"])


@router.get("", response_model=List[BookmarkResponse])
async def get_my_bookmarks(
    current_user: User = Depends(get_current_active_user),
    db: AsyncSession = Depends(get_db)
):
    return await BookmarkService.get_user_bookmarks(db, user_id=current_user.id)


@router.post("", status_code=status.HTTP_201_CREATED)
async def add_bookmark(
    bookmark_in: BookmarkCreate,
    current_user: User = Depends(get_current_active_user),
    db: AsyncSession = Depends(get_db)
):
    bookmark = await BookmarkService.add_bookmark(
        db, user_id=current_user.id, news_id=bookmark_in.news_id
    )
    return {"status": "success", "bookmark_id": str(bookmark.id)}


@router.delete("/{news_id}", status_code=status.HTTP_200_OK)
async def remove_bookmark(
    news_id: uuid.UUID,
    current_user: User = Depends(get_current_active_user),
    db: AsyncSession = Depends(get_db)
):
    removed = await BookmarkService.remove_bookmark(
        db, user_id=current_user.id, news_id=news_id
    )
    if not removed:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Bookmark not found."
        )
    return {"status": "success", "message": "Bookmark removed."}
