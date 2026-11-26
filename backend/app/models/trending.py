import uuid
from datetime import datetime, timezone
from sqlalchemy import String, Integer, Float, DateTime
from sqlalchemy.orm import Mapped, mapped_column
from sqlalchemy.dialects.postgresql import UUID
from app.core.database import Base


class TrendingTopic(Base):
    __tablename__ = "trending_topics"

    id: Mapped[uuid.UUID] = mapped_column(
        UUID(as_uuid=True),
        primary_key=True,
        default=uuid.uuid4,
        index=True
    )
    topic_name: Mapped[str] = mapped_column(String(150), unique=True, index=True, nullable=False)
    article_count: Mapped[int] = mapped_column(Integer, default=1)
    growth_rate: Mapped[float] = mapped_column(Float, default=0.0)
    time_window: Mapped[str] = mapped_column(String(50), default="24h")

    calculated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        default=lambda: datetime.now(timezone.utc)
    )
