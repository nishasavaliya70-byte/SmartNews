import uuid
from typing import Optional, Dict, Any, List
from sqlalchemy.ext.asyncio import AsyncSession
from app.models.prediction import NewsPrediction
from app.ml.classification import predict_category
from app.ml.fake_news_detection import analyze_credibility
from app.schemas.prediction import PredictionResponse


class PredictionService:

    @classmethod
    async def run_full_analysis(
        cls,
        db: Optional[AsyncSession],
        headline: str,
        content: Optional[str] = "",
        news_id: Optional[uuid.UUID] = None,
        user_id: Optional[uuid.UUID] = None
    ) -> PredictionResponse:
        full_text = f"{headline} {content or ''}".strip()

        # 1. Run Category Classification (Module 1)
        cat_result = predict_category(full_text)
        predicted_category = cat_result["predicted_category"]

        # 2. Run Fake News / Credibility Assessment (Module 2)
        cred_result = analyze_credibility(headline=headline, content=content or "")

        # 3. Extract Entity Keywords
        words = [w.strip(".,;:\"'!?") for w in full_text.split() if len(w) > 4]
        unique_keywords = list(dict.fromkeys(words))[:5]

        # 4. Generate Key Takeaways
        key_takeaways = [
            f"Categorized under domain: {predicted_category}.",
            f"Linguistic analysis rated article as '{cred_result['classification_label']}'.",
            f"Confidence level calculated at {round(cred_result['confidence_score'] * 100, 1)}%."
        ]

        prediction_id = uuid.uuid4()

        # 5. Persist into PostgreSQL news_predictions table if db session provided
        if db:
            try:
                db_prediction = NewsPrediction(
                    id=prediction_id,
                    news_id=news_id,
                    user_id=user_id,
                    predicted_category=predicted_category,
                    classification_label=cred_result["classification_label"],
                    confidence_score=cred_result["confidence_score"],
                    model_version=cred_result["model_version"],
                    explanation=cred_result["explanation"]
                )
                db.add(db_prediction)
                await db.commit()
            except Exception:
                await db.rollback()

        return PredictionResponse(
            id=prediction_id,
            news_id=news_id,
            predicted_category=predicted_category,
            classification_label=cred_result["classification_label"],
            confidence_score=cred_result["confidence_score"],
            credibility_score=cred_result["credibility_score"],
            model_version=cred_result["model_version"],
            explanation=cred_result["explanation"],
            suspicious_language_signals=cred_result["suspicious_language_signals"],
            key_takeaways=key_takeaways,
            important_keywords=unique_keywords,
            verification_tips=cred_result["verification_tips"]
        )
