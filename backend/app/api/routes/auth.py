from fastapi import APIRouter, Depends, status
from fastapi.security import OAuth2PasswordRequestForm
from sqlalchemy.ext.asyncio import AsyncSession
from app.core.database import get_db
from app.schemas.auth import Token, LoginRequest, RefreshTokenRequest
from app.schemas.user import UserCreate, UserResponse
from app.services.auth_service import AuthService
from app.models.user import User
from app.api.deps import get_current_active_user

router = APIRouter(prefix="/auth", tags=["Authentication"])


@router.post("/register", response_model=Token, status_code=status.HTTP_201_CREATED)
async def register(
    user_in: UserCreate,
    db: AsyncSession = Depends(get_db)
):
    user = await AuthService.register_user(db, user_in=user_in)
    return AuthService.generate_tokens(str(user.id))


@router.post("/login", response_model=Token)
async def login(
    login_in: LoginRequest,
    db: AsyncSession = Depends(get_db)
):
    user = await AuthService.authenticate_user(
        db, email=login_in.email, password=login_in.password
    )
    return AuthService.generate_tokens(str(user.id))


@router.post("/login/form", response_model=Token, include_in_schema=False)
async def login_oauth2_form(
    form_data: OAuth2PasswordRequestForm = Depends(),
    db: AsyncSession = Depends(get_db)
):
    user = await AuthService.authenticate_user(
        db, email=form_data.username, password=form_data.password
    )
    return AuthService.generate_tokens(str(user.id))


@router.post("/refresh", response_model=Token)
async def refresh_token(
    refresh_in: RefreshTokenRequest,
    db: AsyncSession = Depends(get_db)
):
    return await AuthService.refresh_access_token(db, refresh_in.refresh_token)


@router.get("/me", response_model=UserResponse)
async def get_current_user_profile(
    current_user: User = Depends(get_current_active_user)
):
    return current_user
