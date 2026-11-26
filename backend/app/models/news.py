import uuid
from datetime import datetime, timezone
from typing import List, Optional
from sqlalchemy import String, Text, DateTime, ForeignKey, Index
from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy.dialects.postgresql import UUID
from app.core.database import Base


class NewsArticle(Base):
    __tablename__ = "news_articles"

    id: Mapped[uuid.UUID] = mapped_column(
        UUID(as_uuid=True),
        primary_key=True,
        default=uuid.uuid4,
        index=True
    )
    title: Mapped[str] = mapped_column(String(500), nullable=False)
    description: Mapped[str] = mapped_column(Text, nullable=False)
    content: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    source_name: Mapped[str] = mapped_column(String(150), nullable=False)
    source_url: Mapped[Optional[str]] = mapped_column(String(500), nullable=True)
    article_url: Mapped[str] = mapped_column(String(1000), unique=True, index=True, nullable=False)
    image_url: Mapped[Optional[str]] = mapped_column(String(1000), nullable=True)
    language: Mapped[str] = mapped_column(String(10), default="en")
    country: Mapped[str] = mapped_column(String(10), default="us")

    category_id: Mapped[Optional[uuid.UUID]] = mapped_column(
        UUID(as_uuid=True),
        ForeignKey("news_categories.id", ondelete="SET NULL"),
        nullable=True,
        index=True
    )

    published_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        index=True,
        nullable=False
    )
    fetched_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        default=lambda: datetime.now(timezone.utc)
    )
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        default=lambda: datetime.now(timezone.utc)
    )

    # Relationships
    category: Mapped[Optional["NewsCategory"]] = relationship(
        "NewsCategory", back_populates="articles"
    )
    bookmarks: Mapped[List["Bookmark"]] = relationship(
        "Bookmark", back_populates="article", cascade="all, delete-orphan"
    )
    reading_history: Mapped[List["ReadingHistory"]] = relationship(
        "ReadingHistory", back_populates="article", cascade="all, delete-orphan"
    )
    summaries: Mapped[List["NewsSummary"]] = relationship(
        "NewsSummary", back_populates="article", cascade="all, delete-orphan"
    )
    predictions: Mapped[List["NewsPrediction"]] = relationship(
        "NewsPrediction", back_populates="article", cascade="all, delete-orphan"
    )

    __table_args__ = (
        Index("ix_news_articles_published_category", "published_at", "category_id"),
    )
