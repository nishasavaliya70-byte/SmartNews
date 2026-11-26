from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.core.config import settings
from app.api.routes import auth, users, news, bookmarks, reading_history, ai_analysis
from app.api.routes.trending import summaries_router, trending_router

app = FastAPI(
    title=settings.PROJECT_NAME,
    openapi_url=f"{settings.API_V1_STR}/openapi.json",
    description="SmartNews AI Backend API - Production FastAPI architecture with PostgreSQL, GNews, TF-IDF Cosine Recommendations & NLP Summaries."
)

# CORS Middleware for Android Client communication
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Register API Routers
app.include_router(auth.router, prefix=settings.API_V1_STR)
app.include_router(users.router, prefix=settings.API_V1_STR)
app.include_router(news.router, prefix=settings.API_V1_STR)
app.include_router(bookmarks.router, prefix=settings.API_V1_STR)
app.include_router(reading_history.router, prefix=settings.API_V1_STR)
app.include_router(ai_analysis.router, prefix=settings.API_V1_STR)
app.include_router(summaries_router, prefix=settings.API_V1_STR)
app.include_router(trending_router, prefix=settings.API_V1_STR)


@app.get("/")
def root():
    return {
        "project": "SmartNews AI",
        "status": "operational",
        "version": "1.0.0",
        "docs_url": "/docs"
    }


@app.get(f"{settings.API_V1_STR}/health")
def health_check():
    return {"status": "healthy", "service": "smartnews_backend"}
