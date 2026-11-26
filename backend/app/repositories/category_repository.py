import uuid
from typing import Optional, List
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from app.models.category import NewsCategory


class CategoryRepository:

    @staticmethod
    async def get_by_id(db: AsyncSession, category_id: uuid.UUID) -> Optional[NewsCategory]:
        result = await db.execute(select(NewsCategory).where(NewsCategory.id == category_id))
        return result.scalars().first()

    @staticmethod
    async def get_by_slug(db: AsyncSession, slug: str) -> Optional[NewsCategory]:
        result = await db.execute(select(NewsCategory).where(NewsCategory.slug == slug.lower()))
        return result.scalars().first()

    @staticmethod
    async def get_all(db: AsyncSession) -> List[NewsCategory]:
        result = await db.execute(select(NewsCategory).order_by(NewsCategory.name))
        return list(result.scalars().all())

    @classmethod
    async def get_or_create(cls, db: AsyncSession, name: str, slug: str) -> NewsCategory:
        existing = await cls.get_by_slug(db, slug=slug)
        if existing:
            return existing
        new_cat = NewsCategory(name=name, slug=slug.lower())
        db.add(new_cat)
        await db.commit()
        await db.refresh(new_cat)
        return new_cat
