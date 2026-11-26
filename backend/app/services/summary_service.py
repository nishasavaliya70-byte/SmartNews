import uuid
from typing import Optional, Dict, Any
from sqlalchemy.ext.asyncio import AsyncSession
from app.models.summary import NewsSummary
from app.repositories.news_repository import NewsRepository
from app.ml.summarization import SummarizationEngine
from pydantic import BaseModel


class SummarizeRequest(BaseModel):
    news_id: Optional[uuid.UUID] = None
    text: Optional[str] = None
    summary_type: str = "short" # "short", "detailed", "key_takeaways"


class SummarizeResponse(BaseModel):
    news_id: Optional[uuid.UUID] = None
    summary: str
    summary_type: str
    key_takeaways: list[str] = []
    important_entities: list[str] = []
    main_topic: str = "General"
    model_version: str = "v1.2-TextRank-TFIDF"


class SummaryService:

    @classmethod
    async def summarize_news(
        cls,
        db: AsyncSession,
        request: SummarizeRequest
    ) -> SummarizeResponse:
        content_to_summarize = request.text or ""
        news_id = request.news_id

        if news_id and not content_to_summarize:
            article = await NewsRepository.get_by_id(db, news_id=news_id)
            if article:
                content_to_summarize = article.content or article.description

        output = SummarizationEngine.generate_summary(
            text=content_to_summarize,
            summary_type=request.summary_type
        )

        if news_id and db:
            try:
                db_summary = NewsSummary(
                    id=uuid.uuid4(),
                    news_id=news_id,
                    summary=output["summary"],
                    summary_type=output["summary_type"],
                    model_version=output["model_version"]
                )
                db.add(db_summary)
                await db.commit()
            except Exception:
                await db.rollback()

        return SummarizeResponse(
            news_id=news_id,
            summary=output["summary"],
            summary_type=output["summary_type"],
            key_takeaways=output["key_takeaways"],
            important_entities=output["important_entities"],
            main_topic=output["main_topic"],
            model_version=output["model_version"]
        )
