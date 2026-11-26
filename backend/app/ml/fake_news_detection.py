import os
import joblib
from typing import Dict, Any
from app.ml.preprocessing import clean_text, extract_linguistic_signals

MODEL_PATH = os.path.join(os.path.dirname(__file__), "models", "fake_news_detector.joblib")
_pipeline = None


def get_fake_news_pipeline():
    global _pipeline
    if _pipeline is None:
        if os.path.exists(MODEL_PATH):
            _pipeline = joblib.load(MODEL_PATH)
        else:
            from app.ml.train import train_fake_news_detector
            train_fake_news_detector()
            _pipeline = joblib.load(MODEL_PATH)
    return _pipeline


def analyze_credibility(headline: str, content: str = "") -> Dict[str, Any]:
    full_text = f"{headline} {content}".strip()
    pipeline = get_fake_news_pipeline()
    vectorizer = pipeline["vectorizer"]
    classifier = pipeline["classifier"]

    # Extract stylistic and sensationalist signals
    signals_dict = extract_linguistic_signals(full_text)
    sensational_signals = signals_dict["sensational_signals"]
    attribution_signals = signals_dict["attribution_signals"]

    cleaned = clean_text(full_text)
    if not cleaned:
        credibility_score = 0.50
        classification_label = "Insufficient Text"
        confidence = 0.50
    else:
        vec = vectorizer.transform([cleaned])
        # Model classes: 0 = Suspicious, 1 = Credible
        probs = classifier.predict_proba(vec)[0]
        # Probability of class 1 (credible)
        class_1_idx = list(classifier.classes_).index(1) if 1 in classifier.classes_ else 1
        credibility_score = float(probs[class_1_idx])
        confidence = float(max(probs))

        # Penalty for heavy sensational markers
        if len(sensational_signals) >= 2:
            credibility_score = max(0.15, credibility_score - 0.25)
        elif len(attribution_signals) >= 1:
            credibility_score = min(0.98, credibility_score + 0.08)

        if credibility_score >= 0.70:
            classification_label = "High Credibility Assessment"
        elif credibility_score >= 0.45:
            classification_label = "Moderate Credibility"
        else:
            classification_label = "Suspicious Signals Detected"

    explanation = (
        f"Linguistic evaluation produced a probabilistic credibility score of {round(credibility_score * 100, 1)}%. "
        f"Syntactic analysis found {len(attribution_signals)} journalistic attribution markers and "
        f"{len(sensational_signals)} sensationalist markers."
    )

    return {
        "credibility_score": round(credibility_score, 4),
        "classification_label": classification_label,
        "confidence_score": round(confidence, 4),
        "suspicious_language_signals": sensational_signals if sensational_signals else ["No significant clickbait markers detected"],
        "explanation": explanation,
        "model_version": pipeline.get("version", "1.0.0"),
        "verification_tips": [
            "Check for corroborating wire reports on major international agencies.",
            "Verify primary researcher or official institutional credentials.",
            "Inspect article date and original hosting domain registry."
        ]
    }
