package com.smartnewsai.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartnewsai.core.theme.LightBackground
import com.smartnewsai.core.theme.PrimaryNavy
import com.smartnewsai.core.theme.SecondaryBlue
import com.smartnewsai.presentation.components.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onArticleClick: (String) -> Unit,
    onNavigateToAiAnalyzer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Header & Greeting
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = uiState.greeting + ",",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = uiState.userName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 2.dp,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = PrimaryNavy,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(SecondaryBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            // 2. Search Bar
            item {
                SearchBarComponent(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(18.dp))
            }

            // 3. Category Filter Chips
            item {
                CategoryChipsRow(
                    categories = uiState.categories,
                    selectedCategoryId = uiState.selectedCategoryId,
                    onCategorySelected = { viewModel.onCategorySelected(it) }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // 4. Breaking News (Featured Story)
            item {
                uiState.featuredArticle?.let { article ->
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Breaking Coverage",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Live updates",
                                style = MaterialTheme.typography.labelSmall,
                                color = SecondaryBlue
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        BreakingNewsCard(
                            article = article,
                            onClick = { onArticleClick(article.id) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 5. AI Insights & NLP Summary Banner
            item {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    AiInsightCard(
                        title = "Real-Time NLP Credibility Check",
                        summary = "Automated sentiment and factual language analysis evaluates incoming wire stories for clickbait, hyperbole, and source attribution.",
                        categoryPrediction = "Technology",
                        credibilityScore = 0.94f,
                        onClick = onNavigateToAiAnalyzer
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 6. "For You" Section
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "For You (Personalized)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            items(uiState.forYouArticles) { article ->
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    StandardNewsCard(
                        article = article,
                        onClick = { onArticleClick(article.id) },
                        onBookmarkClick = { viewModel.toggleBookmark(article.id) }
                    )
                }
            }

            // 7. Latest News
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "Latest Wire Stories",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            items(uiState.latestArticles) { article ->
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    StandardNewsCard(
                        article = article,
                        onClick = { onArticleClick(article.id) },
                        onBookmarkClick = { viewModel.toggleBookmark(article.id) }
                    )
                }
            }
        }
    }
}
