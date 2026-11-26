package com.smartnewsai.presentation.screens.bookmarks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartnewsai.core.theme.PrimaryNavy
import com.smartnewsai.presentation.components.EmptyStateView
import com.smartnewsai.presentation.components.SearchBarComponent
import com.smartnewsai.presentation.components.StandardNewsCard

@Composable
fun BookmarksScreen(
    viewModel: BookmarksViewModel,
    onArticleClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    val filteredList = if (uiState.query.isEmpty()) {
        uiState.bookmarkedArticles
    } else {
        uiState.bookmarkedArticles.filter {
            it.title.contains(uiState.query, ignoreCase = true) ||
            it.description.contains(uiState.query, ignoreCase = true)
        }
    }

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
                        text = "Saved Bookmarks",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy
                        )
                    )
                    Text(
                        text = "Articles preserved for offline reading & deep NLP study",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    SearchBarComponent(
                        query = uiState.query,
                        onQueryChange = { viewModel.onQueryChange(it) },
                        placeholderText = "Search saved articles..."
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            if (filteredList.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "No Bookmarks Yet",
                        description = "When you discover interesting stories, tap the bookmark icon to save them to your library.",
                        icon = Icons.Default.BookmarkBorder
                    )
                }
            } else {
                items(filteredList) { article ->
                    Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                        StandardNewsCard(
                            article = article,
                            onClick = { onArticleClick(article.id) },
                            onBookmarkClick = { viewModel.removeBookmark(article.id) }
                        )
                    }
                }
            }
        }
    }
}
