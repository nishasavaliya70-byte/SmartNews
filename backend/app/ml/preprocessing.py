import re
import string
from typing import List, Dict, Any

# Common English stopwords list (lightweight, zero external download dependencies)
STOPWORDS = {
    "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are",
    "aren't", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both",
    "but", "by", "can't", "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't",
    "doing", "don't", "down", "during", "each", "few", "for", "from", "further", "had", "hadn't",
    "has", "hasn't", "have", "haven't", "having", "he", "he'd", "he'll", "he's", "her", "here",
    "here's", "hers", "herself", "him", "himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm",
    "i've", "if", "in", "into", "is", "isn't", "it", "it's", "its", "itself", "let's", "me", "more",
    "most", "mustn't", "my", "myself", "no", "nor", "not", "of", "off", "on", "once", "only", "or",
    "other", "ought", "our", "ours", "ourselves", "out", "over", "own", "same", "shan't", "she",
    "she'd", "she'll", "she's", "should", "shouldn't", "so", "some", "such", "than", "that", "that's",
    "the", "their", "theirs", "them", "themselves", "then", "there", "there's", "these", "they",
    "they'd", "they'll", "they're", "they've", "this", "those", "through", "to", "too", "under",
    "until", "up", "very", "was", "wasn't", "we", "we'd", "we'll", "we're", "we've", "were", "weren't",
    "what", "what's", "when", "when's", "where", "where's", "which", "while", "who", "who's", "whom",
    "why", "why's", "with", "won't", "would", "wouldn't", "you", "you'd", "you'll", "you're", "you've",
    "your", "yours", "yourself", "yourselves"
}

SENSATIONAL_MARKERS = {
    "shocking", "unbelievable", "miracle", "secret", "exposed", "they don't want you to know",
    "cure", "guaranteed", "you won't believe", "conspiracy", "mind-blowing", "panic", "disaster",
    "bombshell", "plot", "scandalous", "hidden truth", "forbidden", "destroy"
}

ATTRIBUTION_MARKERS = {
    "according to", "stated", "confirmed", "reported by", "researchers at", "published in",
    "official statement", "data reveals", "spokesperson", "study conducted by", "symposium"
}


def clean_text(text: str) -> str:
    """Preprocesses raw text: cleans URLs, punctuation, lowercases, and strips extra spaces."""
    if not text:
        return ""
    # Remove URLs
    text = re.sub(r"https?://\S+|www\.\S+", "", text)
    # Remove HTML tags
    text = re.sub(r"<.*?>", "", text)
    # Remove punctuation & numbers
    text = re.sub(r"[^\w\s]", " ", text)
    text = re.sub(r"\d+", " ", text)
    # Lowercase & normalize whitespace
    tokens = text.lower().split()
    # Filter stopwords
    filtered = [token for token in tokens if token not in STOPWORDS and len(token) > 2]
    return " ".join(filtered)


def extract_linguistic_signals(raw_text: str) -> Dict[str, Any]:
    """Analyzes text for stylistic, sensationalist, and attribution signals."""
    if not raw_text:
        return {
            "sensational_signals": [],
            "attribution_signals": [],
            "all_caps_ratio": 0.0,
            "exclamation_count": 0
        }

    exclamation_count = raw_text.count("!")
    question_count = raw_text.count("?")

    words = raw_text.split()
    upper_words = [w for w in words if w.isupper() and len(w) > 2]
    all_caps_ratio = len(upper_words) / max(len(words), 1)

    lowered = raw_text.lower()
    found_sensational = [marker for marker in SENSATIONAL_MARKERS if marker in lowered]
    found_attribution = [marker for marker in ATTRIBUTION_MARKERS if marker in lowered]

    signals = []
    if exclamation_count > 1:
        signals.append(f"Excessive exclamation punctuation ({exclamation_count} occurrences)")
    if all_caps_ratio > 0.08:
        signals.append("High ratio of capitalised clickbait words detected")
    if found_sensational:
        signals.append(f"Sensationalist lexical markers detected: {', '.join(found_sensational[:3])}")

    return {
        "sensational_signals": signals,
        "attribution_signals": found_attribution,
        "all_caps_ratio": round(all_caps_ratio, 3),
        "exclamation_count": exclamation_count
    }
