import pytest
from app.ml.classification import predict_category
from app.ml.fake_news_detection import analyze_credibility
from app.ml.summarization import SummarizationEngine
from app.ml.recommendation import RecommendationEngine


def test_category_classification_tech():
    res = predict_category("Neuromorphic processor architecture demonstrates 10x computational speedup")
    assert "predicted_category" in res
    assert "confidence_score" in res
    assert res["predicted_category"] == "Technology"


def test_fake_news_credibility_signals():
    sensational_headline = "SHOCKING SECRET EXPOSED! Miracle fruit cures all diseases overnight!!"
    res = analyze_credibility(sensational_headline)
    assert res["credibility_score"] < 0.40
    assert res["classification_label"] == "Suspicious Signals Detected"
    assert len(res["suspicious_language_signals"]) > 0


def test_credible_journalistic_headline():
    credible_headline = "According to a clinical study published in Nature, researchers confirmed trial findings."
    res = analyze_credibility(credible_headline)
    assert res["credibility_score"] >= 0.50
    assert "attribution" in res["explanation"].lower()


def test_extractive_summarization():
    text = (
        "Neuromorphic silicon chips promise unprecedented energy efficiency. "
        "Engineers confirmed that resistive RAM arrays compute directly in memory. "
        "Commercial deployment will arrive in consumer devices by 2027. "
        "This completely redefines on-device edge computing."
    )
    res = SummarizationEngine.generate_summary(text, summary_type="short")
    assert "summary" in res
    assert len(res["key_takeaways"]) > 0
    assert len(res["important_entities"]) > 0


def test_recommendation_cosine_similarity():
    candidates = [
        {"id": "1", "title": "Quantum computers and artificial intelligence chips", "description": "Tech news", "category_name": "Technology"},
        {"id": "2", "title": "World athletic marathon results and records", "description": "Sports news", "category_name": "Sports"}
    ]
    recs = RecommendationEngine.recommend_articles(
        user_preference_text="I read about quantum physics, silicon chips, and technology",
        candidate_articles=candidates,
        top_n=2
    )
    assert len(recs) == 2
    # The tech article should have higher recommendation match score
    assert recs[0]["id"] == "1"
    assert recs[0]["recommendation_match_score"] >= recs[1]["recommendation_match_score"]
