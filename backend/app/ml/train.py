import os
import json
import joblib
from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import accuracy_score, precision_recall_fscore_support, confusion_matrix
from app.ml.preprocessing import clean_text
from app.ml.dataset import CATEGORY_DATASET, CREDIBILITY_DATASET

MODEL_DIR = os.path.join(os.path.dirname(__file__), "models")
os.makedirs(MODEL_DIR, exist_ok=True)


def train_category_classifier():
    print("Training News Category Classification Model...")
    texts = [clean_text(item[0]) for item in CATEGORY_DATASET]
    labels = [item[1] for item in CATEGORY_DATASET]

    X_train, X_test, y_train, y_test = train_test_split(
        texts, labels, test_size=0.25, random_state=42, stratify=labels
    )

    vectorizer = TfidfVectorizer(ngram_range=(1, 2), max_features=3000)
    X_train_vec = vectorizer.fit_transform(X_train)
    X_test_vec = vectorizer.transform(X_test)

    classifier = LogisticRegression(C=1.5, max_iter=500, random_state=42)
    classifier.fit(X_train_vec, y_train)

    y_pred = classifier.predict(X_test_vec)
    acc = accuracy_score(y_test, y_pred)
    precision, recall, f1, _ = precision_recall_fscore_support(
        y_test, y_pred, average="weighted", zero_division=0
    )

    model_pipeline = {
        "vectorizer": vectorizer,
        "classifier": classifier,
        "classes": classifier.classes_.tolist(),
        "version": "1.0.0"
    }
    model_path = os.path.join(MODEL_DIR, "category_classifier.joblib")
    joblib.dump(model_pipeline, model_path)

    metrics = {
        "accuracy": round(float(acc), 4),
        "precision": round(float(precision), 4),
        "recall": round(float(recall), 4),
        "f1_score": round(float(f1), 4)
    }
    print(f"Category Classifier Trained. Accuracy: {metrics['accuracy']}, F1: {metrics['f1_score']}")
    return metrics


def train_fake_news_detector():
    print("Training Fake News / Credibility Assessment Model...")
    texts = [clean_text(item[0]) for item in CREDIBILITY_DATASET]
    labels = [item[1] for item in CREDIBILITY_DATASET]

    X_train, X_test, y_train, y_test = train_test_split(
        texts, labels, test_size=0.25, random_state=42, stratify=labels
    )

    vectorizer = TfidfVectorizer(ngram_range=(1, 2), max_features=2000)
    X_train_vec = vectorizer.fit_transform(X_train)
    X_test_vec = vectorizer.transform(X_test)

    classifier = LogisticRegression(C=1.0, max_iter=500, random_state=42)
    classifier.fit(X_train_vec, y_train)

    y_pred = classifier.predict(X_test_vec)
    acc = accuracy_score(y_test, y_pred)
    precision, recall, f1, _ = precision_recall_fscore_support(
        y_test, y_pred, average="binary", zero_division=0
    )
    cm = confusion_matrix(y_test, y_pred).tolist()

    model_pipeline = {
        "vectorizer": vectorizer,
        "classifier": classifier,
        "version": "1.0.0"
    }
    model_path = os.path.join(MODEL_DIR, "fake_news_detector.joblib")
    joblib.dump(model_pipeline, model_path)

    metrics = {
        "accuracy": round(float(acc), 4),
        "precision": round(float(precision), 4),
        "recall": round(float(recall), 4),
        "f1_score": round(float(f1), 4),
        "confusion_matrix": cm
    }
    print(f"Fake News Detector Trained. Accuracy: {metrics['accuracy']}, F1: {metrics['f1_score']}")
    return metrics


if __name__ == "__main__":
    cat_metrics = train_category_classifier()
    fake_metrics = train_fake_news_detector()

    all_metrics = {
        "category_classifier": cat_metrics,
        "fake_news_detector": fake_metrics
    }
    metrics_path = os.path.join(MODEL_DIR, "evaluation_metrics.json")
    with open(metrics_path, "w", encoding="utf-8") as f:
        json.dump(all_metrics, f, indent=4)
    print("All ML Models Trained & Serialized successfully!")
