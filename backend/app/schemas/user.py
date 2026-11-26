import uuid
from datetime import datetime
from typing import Optional, List
from pydantic import BaseModel, EmailStr, Field


class UserBase(BaseModel):
    email: EmailStr
    full_name: str = Field(..., min_length=2, max_length=120)


class UserCreate(UserBase):
    password: str = Field(..., min_length=6, max_length=128)


class UserUpdate(BaseModel):
    full_name: Optional[str] = Field(None, min_length=2, max_length=120)
    profile_image: Optional[str] = None


class UserResponse(UserBase):
    id: uuid.UUID
    profile_image: Optional[str] = None
    is_active: bool
    created_at: datetime
    selected_interests: List[str] = []

    class Config:
        from_attributes = True
