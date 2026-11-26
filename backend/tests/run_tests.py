import unittest
from app.ml.classification import predict_category
from app.ml.fake_news_detection import analyze_credibility
from app.ml.summarization import SummarizationEngine
from app.ml.recommendation import RecommendationEngine
from app.ml.trending_analysis import TrendingAnalysisEngine


class TestSmartNewsAI(unittest.TestCase):

    def test_category_classification_tech(self):
        res = predict_category("Neuromorphic processor architecture demonstrates 10x computational speedup")
        self.assertIn("predicted_category", res)
        self.assertIn("confidence_score", res)
        self.assertEqual(res["predicted_category"], "Technology")

    def test_fake_news_credibility_signals(self):
        sensational_headline = "SHOCKING SECRET EXPOSED! Miracle fruit cures all diseases overnight!!"
        res = analyze_credibility(sensational_headline)
        self.assertLess(res["credibility_score"], 0.40)
        self.assertEqual(res["classification_label"], "Suspicious Signals Detected")
        self.assertGreater(len(res["suspicious_language_signals"]), 0)

    def test_credible_journalistic_headline(self):
        credible_headline = "According to a clinical study published in Nature, researchers confirmed trial findings."
        res = analyze_credibility(credible_headline)
        self.assertGreaterEqual(res["credibility_score"], 0.50)
        self.assertIn("attribution", res["explanation"].lower())

    def test_extractive_summarization(self):
        text = (
            "Neuromorphic silicon chips promise unprecedented energy efficiency. "
            "Engineers confirmed that resistive RAM arrays compute directly in memory. "
            "Commercial deployment will arrive in consumer devices by 2027. "
            "This completely redefines on-device edge computing."
        )
        res = SummarizationEngine.generate_summary(text, summary_type="short")
        self.assertIn("summary", res)
        self.assertGreater(len(res["key_takeaways"]), 0)
        self.assertGreater(len(res["important_entities"]), 0)

    def test_recommendation_cosine_similarity(self):
        candidates = [
            {"id": "1", "title": "Quantum computers and artificial intelligence chips", "description": "Tech news", "category_name": "Technology"},
            {"id": "2", "title": "World athletic marathon results and records", "description": "Sports news", "category_name": "Sports"}
        ]
        recs = RecommendationEngine.recommend_articles(
            user_preference_text="I read about quantum physics, silicon chips, and technology",
            candidate_articles=candidates,
            top_n=2
        )
        self.assertEqual(len(recs), 2)
        self.assertEqual(recs[0]["id"], "1")
        self.assertGreaterEqual(recs[0]["recommendation_match_score"], recs[1]["recommendation_match_score"])

    def test_trending_topic_velocity(self):
        candidates = [
            {"title": "Breakthrough in solid state battery storage", "category_name": "Technology"},
            {"title": "Solid state battery manufacturing expands", "category_name": "Technology"}
        ]
        trending = TrendingAnalysisEngine.analyze_emerging_topics(candidates)
        self.assertIn("trending_topics", trending)
        self.assertIn("trending_categories", trending)
        self.assertGreater(len(trending["trending_topics"]), 0)


if __name__ == "__main__":
    unittest.main()
