from fastapi import APIRouter, Depends, status
from sqlalchemy.ext.asyncio import AsyncSession
from app.core.database import get_db
from app.schemas.prediction import PredictionRequest, PredictionResponse
from app.services.prediction_service import PredictionService
from app.ml.classification import predict_category
from app.ml.fake_news_detection import analyze_credibility

router = APIRouter(prefix="/ai", tags=["AI Analysis & Machine Learning"])


@router.post("/classify", status_code=status.HTTP_200_OK)
async def classify_category(request: PredictionRequest):
    """Module 1: Classifies text into one of 8 news categories using TF-IDF + Logistic Regression."""
    text = f"{request.headline or ''} {request.content or ''}".strip()
    return predict_category(text)


@router.post("/fake-news-detection", status_code=status.HTTP_200_OK)
async def detect_fake_news(request: PredictionRequest):
    """Module 2: Evaluates text for sensationalism, linguistic markers, and probabilistic credibility."""
    return analyze_credibility(
        headline=request.headline or "",
        content=request.content or ""
    )


@router.post("/analyze", response_model=PredictionResponse, status_code=status.HTTP_200_OK)
async def run_ai_analysis(
    request: PredictionRequest,
    db: AsyncSession = Depends(get_db)
):
    """Combined Deep AI Analysis: Category Classification, Credibility Scoring, and Linguistic Signals."""
    return await PredictionService.run_full_analysis(
        db=db,
        headline=request.headline or "Submitted News Analysis",
        content=request.content,
        news_id=request.news_id
    )
