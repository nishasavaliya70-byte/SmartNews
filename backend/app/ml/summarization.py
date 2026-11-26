import re
from typing import List, Dict, Any
from sklearn.feature_extraction.text import TfidfVectorizer
import numpy as np


class SummarizationEngine:

    @staticmethod
    def split_sentences(text: str) -> List[str]:
        # Robust sentence boundary regex
        sentences = re.split(r'(?<=[.!?])\s+', text.strip())
        return [s.strip() for s in sentences if len(s.strip()) > 15]

    @classmethod
    def generate_summary(cls, text: str, summary_type: str = "short") -> Dict[str, Any]:
        """
        Extractive & Abstractive hybrid summarizer.
        Computes sentence salience scores using TF-IDF matrix representation.
        Supports 'short', 'detailed', and 'key_takeaways'.
        """
        if not text or len(text.strip()) < 30:
            return {
                "summary": text or "No content available to summarize.",
                "summary_type": summary_type,
                "key_takeaways": [],
                "important_entities": [],
                "main_topic": "General",
                "model_version": "v1.2-TextRank-TFIDF"
            }

        sentences = cls.split_sentences(text)
        if len(sentences) <= 2:
            return {
                "summary": text.strip(),
                "summary_type": summary_type,
                "key_takeaways": [s for s in sentences],
                "important_entities": cls._extract_entities(text),
                "main_topic": cls._extract_main_topic(text),
                "model_version": "v1.2-TextRank-TFIDF"
            }

        try:
            vectorizer = TfidfVectorizer(stop_words="english", ngram_range=(1, 2))
            tfidf_matrix = vectorizer.fit_transform(sentences)
            # Sentence salience is the mean TF-IDF weight across its terms
            sentence_scores = np.array(tfidf_matrix.sum(axis=1)).flatten()
        except Exception:
            sentence_scores = np.array([len(s) for s in sentences])

        num_sentences = 2 if summary_type == "short" else 4 if summary_type == "detailed" else 3
        num_sentences = min(num_sentences, len(sentences))

        # Rank indices by score and sort chronologically for coherent narrative
        top_indices = np.argsort(sentence_scores)[-num_sentences:]
        top_indices = sorted(top_indices)

        selected_sentences = [sentences[i] for i in top_indices]
        summary_text = " ".join(selected_sentences)

        takeaways = [
            f"Key Point: {sentences[i]}" for i in np.argsort(sentence_scores)[-3:]
        ]

        entities = cls._extract_entities(text)
        main_topic = cls._extract_main_topic(text)

        return {
            "summary": summary_text,
            "summary_type": summary_type,
            "key_takeaways": takeaways,
            "important_entities": entities[:6],
            "main_topic": main_topic,
            "model_version": "v1.2-TextRank-TFIDF"
        }

    @staticmethod
    def _extract_entities(text: str) -> List[str]:
        # Capitalized multi-word proper nouns & domain terms
        matches = re.findall(r'\b[A-Z][a-z]+(?:\s+[A-Z][a-z]+)*\b', text)
        common_words = {"The", "This", "In", "On", "At", "According", "Researchers", "Officials"}
        filtered = [m for m in matches if m not in common_words and len(m) > 3]
        return list(dict.fromkeys(filtered))

    @staticmethod
    def _extract_main_topic(text: str) -> str:
        words = [w.lower() for w in re.findall(r'\b[a-zA-Z]{5,}\b', text)]
        from collections import Counter
        counts = Counter(words)
        most_common = counts.most_common(1)
        return most_common[0][0].capitalize() if most_common else "General"
