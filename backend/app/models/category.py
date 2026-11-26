import uuid
from datetime import datetime, timezone
from typing import List, Optional
from sqlalchemy import String, Text, DateTime
from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy.dialects.postgresql import UUID
from app.core.database import Base


class NewsCategory(Base):
    __tablename__ = "news_categories"

    id: Mapped[uuid.UUID] = mapped_column(
        UUID(as_uuid=True),
        primary_key=True,
        default=uuid.uuid4,
        index=True
    )
    name: Mapped[str] = mapped_column(String(100), nullable=False)
    slug: Mapped[str] = mapped_column(String(100), unique=True, index=True, nullable=False)
    description: Mapped[Optional[str]] = mapped_column(Text, nullable=True)

    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        default=lambda: datetime.now(timezone.utc)
    )

    # Relationships
    articles: Mapped[List["NewsArticle"]] = relationship(
        "NewsArticle", back_populates="category"
    )
    interested_users: Mapped[List["UserInterest"]] = relationship(
        "UserInterest", back_populates="category", cascade="all, delete-orphan"
    )
