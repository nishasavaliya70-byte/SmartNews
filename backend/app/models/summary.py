import uuid
from datetime import datetime, timezone
from sqlalchemy import String, Text, DateTime, ForeignKey
from sqlalchemy.orm import Mapped, mapped_column, relationship
from sqlalchemy.dialects.postgresql import UUID
from app.core.database import Base


class NewsSummary(Base):
    __tablename__ = "news_summaries"

    id: Mapped[uuid.UUID] = mapped_column(
        UUID(as_uuid=True),
        primary_key=True,
        default=uuid.uuid4,
        index=True
    )
    news_id: Mapped[uuid.UUID] = mapped_column(
        UUID(as_uuid=True),
        ForeignKey("news_articles.id", ondelete="CASCADE"),
        nullable=False,
        index=True
    )

    summary: Mapped[str] = mapped_column(Text, nullable=False)
    summary_type: Mapped[str] = mapped_column(String(50), default="short") # "short", "detailed", "key_takeaways"
    model_version: Mapped[str] = mapped_column(String(50), default="1.0.0")

    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        default=lambda: datetime.now(timezone.utc)
    )

    # Relationships
    article: Mapped["NewsArticle"] = relationship("NewsArticle", back_populates="summaries")
