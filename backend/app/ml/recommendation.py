from typing import List, Dict, Any, Optional
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from app.ml.preprocessing import clean_text


class RecommendationEngine:

    @classmethod
    def recommend_articles(
        cls,
        user_preference_text: str,
        candidate_articles: List[Dict[str, Any]],
        exclude_article_ids: Optional[List[str]] = None,
        top_n: int = 5
    ) -> List[Dict[str, Any]]:
        """
        Content-Based Filtering: Computes TF-IDF Cosine Similarity between user interest vector
        and candidate articles corpus.
        Excludes already read articles.
        """
        exclude_set = set(str(i) for i in (exclude_article_ids or []))
        eligible_articles = [a for a in candidate_articles if str(a.get("id")) not in exclude_set]

        if not eligible_articles:
            return candidate_articles[:top_n]

        cleaned_user = clean_text(user_preference_text)
        if not cleaned_user:
            return eligible_articles[:top_n]

        corpus = [cleaned_user] + [
            clean_text(f"{a.get('title', '')} {a.get('description', '')} {a.get('category_name', '')}")
            for a in eligible_articles
        ]

        try:
            vectorizer = TfidfVectorizer(ngram_range=(1, 2), max_features=3000)
            tfidf_matrix = vectorizer.fit_transform(corpus)

            # Cosine similarity between user vector (index 0) and article vectors (1..N)
            user_vec = tfidf_matrix[0:1]
            article_vecs = tfidf_matrix[1:]
            similarities = cosine_similarity(user_vec, article_vecs).flatten()

            # Rank by descending similarity
            ranked_indices = similarities.argsort()[::-1][:top_n]

            results = []
            for idx in ranked_indices:
                art = eligible_articles[idx]
                score = float(similarities[idx])
                art_copy = dict(art)
                art_copy["recommendation_match_score"] = round(score, 3)
                results.append(art_copy)
            return results

        except Exception:
            return eligible_articles[:top_n]
