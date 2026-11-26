package com.smartnewsai.presentation.screens.explore

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartnewsai.core.theme.PrimaryNavy
import com.smartnewsai.core.theme.SecondaryBlue
import com.smartnewsai.domain.model.Category
import com.smartnewsai.presentation.components.CategoryChipsRow
import com.smartnewsai.presentation.components.SearchBarComponent
import com.smartnewsai.presentation.components.StandardNewsCard

@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel,
    onArticleClick: (String) -> Unit,
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
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Text(
                        text = "Explore & Discover",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy
                        )
                    )
                    Text(
                        text = "Filter across global channels, trending topics & NLP entities",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    SearchBarComponent(
                        query = uiState.query,
                        onQueryChange = { viewModel.onQueryChange(it) },
                        onFilterClick = {}
                    )
                }
            }

            // Trending Keywords Pills
            item {
                Column {
                    Text(
                        text = "Trending Keywords",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.trendingKeywords) { keyword ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SecondaryBlue.copy(alpha = 0.10f),
                                modifier = Modifier.clickable { viewModel.onKeywordClick(keyword) }
                            ) {
                                Text(
                                    text = "#$keyword",
                                    color = SecondaryBlue,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Category Chips
            item {
                CategoryChipsRow(
                    categories = Category.predefinedCategories,
                    selectedCategoryId = uiState.selectedCategory,
                    onCategorySelected = { viewModel.onCategorySelect(it.id) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Article List
            item {
                Text(
                    text = "Discovered Articles (${uiState.articles.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(uiState.articles) { article ->
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
