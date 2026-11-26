from app.models.user import User
from app.models.category import NewsCategory
from app.models.news import NewsArticle
from app.models.bookmark import Bookmark
from app.models.reading_history import ReadingHistory
from app.models.user_interest import UserInterest
from app.models.prediction import NewsPrediction
from app.models.summary import NewsSummary
from app.models.trending import TrendingTopic

__all__ = [
    "User",
    "NewsCategory",
    "NewsArticle",
    "Bookmark",
    "ReadingHistory",
    "UserInterest",
    "NewsPrediction",
    "NewsSummary",
    "TrendingTopic"
]
