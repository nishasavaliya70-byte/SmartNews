from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from app.core.database import get_db
from app.schemas.user import UserResponse, UserUpdate
from app.models.user import User
from app.repositories.user_repository import UserRepository
from app.api.deps import get_current_active_user

router = APIRouter(prefix="/users", tags=["Users"])


@router.get("/profile", response_model=UserResponse)
async def get_profile(
    current_user: User = Depends(get_current_active_user)
):
    return current_user


@router.patch("/profile", response_model=UserResponse)
async def update_profile(
    user_update: UserUpdate,
    current_user: User = Depends(get_current_active_user),
    db: AsyncSession = Depends(get_db)
):
    updated = await UserRepository.update(db, user=current_user, user_in=user_update)
    return updated
