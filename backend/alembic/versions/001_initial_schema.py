"""001_initial_schema

Revision ID: 001_initial_schema
Revises: 
Create Date: 2026-11-26 15:00:00

"""
from typing import Sequence, Union
from alembic import op
import sqlalchemy as sa
from sqlalchemy.dialects import postgresql

revision: str = "001_initial_schema"
down_revision: Union[str, None] = None
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    # 1. USERS TABLE
    op.create_table(
        "users",
        sa.Column("id", postgresql.UUID(as_uuid=True), primary_key=True),
        sa.Column("full_name", sa.String(length=120), nullable=False),
        sa.Column("email", sa.String(length=255), nullable=False),
        sa.Column("hashed_password", sa.String(length=255), nullable=False),
        sa.Column("profile_image", sa.String(length=500), nullable=True),
        sa.Column("is_active", sa.Boolean(), nullable=False, default=True),
        sa.Column("created_at", sa.DateTime(timezone=True), nullable=False),
        sa.Column("updated_at", sa.DateTime(timezone=True), nullable=False),
    )
    op.create_index("ix_users_id", "users", ["id"], unique=False)
    op.create_index("ix_users_email", "users", ["email"], unique=True)

    # 2. NEWS_CATEGORIES TABLE
    op.create_table(
        "news_categories",
        sa.Column("id", postgresql.UUID(as_uuid=True), primary_key=True),
        sa.Column("name", sa.String(length=100), nullable=False),
        sa.Column("slug", sa.String(length=100), nullable=False),
        sa.Column("description", sa.Text(), nullable=True),
        sa.Column("created_at", sa.DateTime(timezone=True), nullable=False),
    )
    op.create_index("ix_news_categories_id", "news_categories", ["id"], unique=False)
    op.create_index("ix_news_categories_slug", "news_categories", ["slug"], unique=True)

    # 3. NEWS_ARTICLES TABLE
    op.create_table(
        "news_articles",
        sa.Column("id", postgresql.UUID(as_uuid=True), primary_key=True),
        sa.Column("title", sa.String(length=500), nullable=False),
        sa.Column("description", sa.Text(), nullable=False),
        sa.Column("content", sa.Text(), nullable=True),
        sa.Column("source_name", sa.String(length=150), nullable=False),
        sa.Column("source_url", sa.String(length=500), nullable=True),
        sa.Column("article_url", sa.String(length=1000), nullable=False),
        sa.Column("image_url", sa.String(length=1000), nullable=True),
        sa.Column("language", sa.String(length=10), nullable=False, default="en"),
        sa.Column("country", sa.String(length=10), nullable=False, default="us"),
        sa.Column("category_id", postgresql.UUID(as_uuid=True), sa.ForeignKey("news_categories.id", ondelete="SET NULL"), nullable=True),
        sa.Column("published_at", sa.DateTime(timezone=True), nullable=False),
        sa.Column("fetched_at", sa.DateTime(timezone=True), nullable=False),
        sa.Column("created_at", sa.DateTime(timezone=True), nullable=False),
    )
    op.create_index("ix_news_articles_id", "news_articles", ["id"], unique=False)
    op.create_index("ix_news_articles_article_url", "news_articles", ["article_url"], unique=True)
    op.create_index("ix_news_articles_category_id", "news_articles", ["category_id"], unique=False)
    op.create_index("ix_news_articles_published_at", "news_articles", ["published_at"], unique=False)
    op.create_index("ix_news_articles_published_category", "news_articles", ["published_at", "category_id"], unique=False)

    # 4. BOOKMARKS TABLE
    op.create_table(
        "bookmarks",
        sa.Column("id", postgresql.UUID(as_uuid=True), primary_key=True),
        sa.Column("user_id", postgresql.UUID(as_uuid=True), sa.ForeignKey("users.id", ondelete="CASCADE"), nullable=False),
        sa.Column("news_id", postgresql.UUID(as_uuid=True), sa.ForeignKey("news_articles.id", ondelete="CASCADE"), nullable=False),
        sa.Column("created_at", sa.DateTime(timezone=True), nullable=False),
        sa.UniqueConstraint("user_id", "news_id", name="uq_user_news_bookmark")
    )
    op.create_index("ix_bookmarks_id", "bookmarks", ["id"], unique=False)
    op.create_index("ix_bookmarks_user_id", "bookmarks", ["user_id"], unique=False)
    op.create_index("ix_bookmarks_news_id", "bookmarks", ["news_id"], unique=False)

    # 5. READING_HISTORY TABLE
    op.create_table(
        "reading_history",
        sa.Column("id", postgresql.UUID(as_uuid=True), primary_key=True),
        sa.Column("user_id", postgresql.UUID(as_uuid=True), sa.ForeignKey("users.id", ondelete="CASCADE"), nullable=False),
        sa.Column("news_id", postgresql.UUID(as_uuid=True), sa.ForeignKey("news_articles.id", ondelete="CASCADE"), nullable=False),
        sa.Column("viewed_at", sa.DateTime(timezone=True), nullable=False),
    )
    op.create_index("ix_reading_history_id", "reading_history", ["id"], unique=False)
    op.create_index("ix_reading_history_user_id", "reading_history", ["user_id"], unique=False)
    op.create_index("ix_reading_history_news_id", "reading_history", ["news_id"], unique=False)

    # 6. USER_INTERESTS TABLE
    op.create_table(
        "user_interests",
        sa.Column("id", postgresql.UUID(as_uuid=True), primary_key=True),
        sa.Column("user_id", postgresql.UUID(as_uuid=True), sa.ForeignKey("users.id", ondelete="CASCADE"), nullable=False),
        sa.Column("category_id", postgresql.UUID(as_uuid=True), sa.ForeignKey("news_categories.id", ondelete="CASCADE"), nullable=False),
        sa.Column("created_at", sa.DateTime(timezone=True), nullable=False),
        sa.UniqueConstraint("user_id", "category_id", name="uq_user_category_interest")
    )
    op.create_index("ix_user_interests_id", "user_interests", ["id"], unique=False)
    op.create_index("ix_user_interests_user_id", "user_interests", ["user_id"], unique=False)
    op.create_index("ix_user_interests_category_id", "user_interests", ["category_id"], unique=False)

    # 7. NEWS_PREDICTIONS TABLE
    op.create_table(
        "news_predictions",
        sa.Column("id", postgresql.UUID(as_uuid=True), primary_key=True),
        sa.Column("news_id", postgresql.UUID(as_uuid=True), sa.ForeignKey("news_articles.id", ondelete="CASCADE"), nullable=True),
        sa.Column("user_id", postgresql.UUID(as_uuid=True), sa.ForeignKey("users.id", ondelete="SET NULL"), nullable=True),
        sa.Column("predicted_category", sa.String(length=100), nullable=False),
        sa.Column("classification_label", sa.String(length=100), nullable=False),
        sa.Column("confidence_score", sa.Float(), nullable=False),
        sa.Column("model_version", sa.String(length=50), nullable=False, default="1.0.0"),
        sa.Column("explanation", sa.Text(), nullable=True),
        sa.Column("created_at", sa.DateTime(timezone=True), nullable=False),
    )
    op.create_index("ix_news_predictions_id", "news_predictions", ["id"], unique=False)
    op.create_index("ix_news_predictions_news_id", "news_predictions", ["news_id"], unique=False)
    op.create_index("ix_news_predictions_user_id", "news_predictions", ["user_id"], unique=False)

    # 8. NEWS_SUMMARIES TABLE
    op.create_table(
        "news_summaries",
        sa.Column("id", postgresql.UUID(as_uuid=True), primary_key=True),
        sa.Column("news_id", postgresql.UUID(as_uuid=True), sa.ForeignKey("news_articles.id", ondelete="CASCADE"), nullable=False),
        sa.Column("summary", sa.Text(), nullable=False),
        sa.Column("summary_type", sa.String(length=50), nullable=False, default="short"),
        sa.Column("model_version", sa.String(length=50), nullable=False, default="1.0.0"),
        sa.Column("created_at", sa.DateTime(timezone=True), nullable=False),
    )
    op.create_index("ix_news_summaries_id", "news_summaries", ["id"], unique=False)
    op.create_index("ix_news_summaries_news_id", "news_summaries", ["news_id"], unique=False)

    # 9. TRENDING_TOPICS TABLE
    op.create_table(
        "trending_topics",
        sa.Column("id", postgresql.UUID(as_uuid=True), primary_key=True),
        sa.Column("topic_name", sa.String(length=150), nullable=False),
        sa.Column("article_count", sa.Integer(), nullable=False, default=1),
        sa.Column("growth_rate", sa.Float(), nullable=False, default=0.0),
        sa.Column("time_window", sa.String(length=50), nullable=False, default="24h"),
        sa.Column("calculated_at", sa.DateTime(timezone=True), nullable=False),
    )
    op.create_index("ix_trending_topics_id", "trending_topics", ["id"], unique=False)
    op.create_index("ix_trending_topics_topic_name", "trending_topics", ["topic_name"], unique=True)


def downgrade() -> None:
    op.drop_table("trending_topics")
    op.drop_table("news_summaries")
    op.drop_table("news_predictions")
    op.drop_table("user_interests")
    op.drop_table("reading_history")
    op.drop_table("bookmarks")
    op.drop_table("news_articles")
    op.drop_table("news_categories")
    op.drop_table("users")
