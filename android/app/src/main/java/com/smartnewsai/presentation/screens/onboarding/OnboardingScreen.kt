package com.smartnewsai.presentation.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartnewsai.core.theme.*
import com.smartnewsai.domain.model.OnboardingItem
import com.smartnewsai.presentation.components.PagerIndicator
import com.smartnewsai.presentation.components.PrimaryButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onFinishOnboarding: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { viewModel.pages.size })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.navigateToAuth.collect {
            onFinishOnboarding()
        }
    }

    val pageIcons = listOf<ImageVector>(
        Icons.Default.Language,
        Icons.Default.Psychology,
        Icons.Default.AutoAwesome
    )

    Scaffold(
        containerColor = LightBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Top App Bar with Skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SMARTNEWS AI",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
                    )
                )

                if (pagerState.currentPage < viewModel.pages.size - 1) {
                    TextButton(
                        onClick = { viewModel.completeOnboarding() }
                    ) {
                        Text(
                            text = "Skip",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = SecondaryBlue,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }

            // Pager content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { pageIndex ->
                OnboardingPageContent(
                    item = viewModel.pages[pageIndex],
                    icon = pageIcons[pageIndex]
                )
            }

            // Bottom Navigation & Buttons Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PagerIndicator(
                    pageCount = viewModel.pages.size,
                    currentPage = pagerState.currentPage,
                    activeColor = SecondaryBlue,
                    inactiveColor = LightBorder
                )

                Spacer(modifier = Modifier.height(28.dp))

                if (pagerState.currentPage < viewModel.pages.size - 1) {
                    PrimaryButton(
                        text = "Next",
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    )
                } else {
                    PrimaryButton(
                        text = "Get Started",
                        onClick = {
                            viewModel.completeOnboarding()
                        },
                        containerColor = PrimaryNavy
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(
    item: OnboardingItem,
    icon: ImageVector
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Feature Illustration Card
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            PrimaryNavy,
                            SecondaryBlue
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AccentCyan,
                modifier = Modifier.size(96.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Badge Pill
        Surface(
            color = SecondaryBlue.copy(alpha = 0.12f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = item.badgeText,
                color = SecondaryBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = item.title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = PrimaryNavy
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Description
        Text(
            text = item.description,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = SecondaryText,
                lineHeight = 22.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}
