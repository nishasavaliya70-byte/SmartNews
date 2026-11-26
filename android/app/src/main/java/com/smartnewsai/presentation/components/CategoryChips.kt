package com.smartnewsai.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartnewsai.core.theme.PrimaryNavy
import com.smartnewsai.core.theme.SecondaryBlue
import com.smartnewsai.domain.model.Category

@Composable
fun CategoryChipsRow(
    categories: List<Category>,
    selectedCategoryId: String,
    onCategorySelected: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        items(categories) { category ->
            val isSelected = category.id == selectedCategoryId
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) SecondaryBlue else MaterialTheme.colorScheme.surface,
                shadowElevation = if (isSelected) 3.dp else 1.dp,
                modifier = Modifier.clickable { onCategorySelected(category) }
            ) {
                Text(
                    text = category.name,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                )
            }
        }
    }
}
