import os
import joblib
from typing import Dict, Any
from app.ml.preprocessing import clean_text

MODEL_PATH = os.path.join(os.path.dirname(__file__), "models", "category_classifier.joblib")
_pipeline = None


def get_category_pipeline():
    global _pipeline
    if _pipeline is None:
        if os.path.exists(MODEL_PATH):
            _pipeline = joblib.load(MODEL_PATH)
        else:
            from app.ml.train import train_category_classifier
            train_category_classifier()
            _pipeline = joblib.load(MODEL_PATH)
    return _pipeline


def predict_category(text: str) -> Dict[str, Any]:
    pipeline = get_category_pipeline()
    vectorizer = pipeline["vectorizer"]
    classifier = pipeline["classifier"]

    cleaned = clean_text(text)
    if not cleaned:
        return {
            "predicted_category": "General",
            "confidence_score": 0.50,
            "model_version": pipeline.get("version", "1.0.0")
        }

    vec = vectorizer.transform([cleaned])
    predicted_cat = classifier.predict(vec)[0]
    probabilities = classifier.predict_proba(vec)[0]
    confidence = float(max(probabilities))

    return {
        "predicted_category": str(predicted_cat),
        "confidence_score": round(confidence, 4),
        "model_version": pipeline.get("version", "1.0.0")
    }
