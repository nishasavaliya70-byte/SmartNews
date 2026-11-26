package com.smartnewsai.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartnewsai.core.theme.PrimaryNavy
import com.smartnewsai.core.theme.SecondaryBlue

sealed class NavigationTab(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : NavigationTab("tab_home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Explore : NavigationTab("tab_explore", "Explore", Icons.Filled.Explore, Icons.Outlined.Explore)
    object AiAnalyze : NavigationTab("tab_ai_analyze", "AI Analyze", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome)
    object Bookmarks : NavigationTab("tab_bookmarks", "Bookmarks", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder)
    object Profile : NavigationTab("tab_profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)

    companion object {
        val items = listOf(Home, Explore, AiAnalyze, Bookmarks, Profile)
    }
}

@Composable
fun AppBottomNavigation(
    selectedRoute: String,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        NavigationTab.items.forEach { tab ->
            val isSelected = selectedRoute == tab.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.title
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SecondaryBlue,
                    selectedTextColor = SecondaryBlue,
                    indicatorColor = SecondaryBlue.copy(alpha = 0.12f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
