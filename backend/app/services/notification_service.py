import logging
from typing import Optional, List, Dict, Any

logger = logging.getLogger(__name__)


class NotificationService:
    """
    Firebase Cloud Messaging (FCM) push notification dispatcher service.
    Handles targeted category alerts and high-priority breaking news broadcasts.
    """

    @classmethod
    async def send_breaking_news_alert(
        cls,
        title: str,
        body: str,
        news_id: str,
        category: Optional[str] = None
    ) -> Dict[str, Any]:
        payload = {
            "title": f"🚨 BREAKING: {title}",
            "body": body[:120] + "..." if len(body) > 120 else body,
            "data": {
                "news_id": news_id,
                "category": category or "General",
                "type": "breaking_news"
            }
        }
        # Logs dispatch for academic evaluation & simulator testing
        logger.info(f"FCM Notification Dispatched: {payload}")
        return {"status": "dispatched", "payload": payload}

    @classmethod
    async def send_topic_velocity_alert(
        cls,
        topic_name: str,
        growth_rate: float
    ) -> Dict[str, Any]:
        payload = {
            "title": f"📈 Trending Topic Alert: #{topic_name}",
            "body": f"Coverage of #{topic_name} increased by {int(growth_rate * 100)}% in the last 24 hours.",
            "data": {
                "topic": topic_name,
                "type": "topic_trend"
            }
        }
        logger.info(f"FCM Trending Alert Dispatched: {payload}")
        return {"status": "dispatched", "payload": payload}
