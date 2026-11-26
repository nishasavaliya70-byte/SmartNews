package com.smartnewsai.presentation.screens.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ShortText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartnewsai.core.theme.*
import com.smartnewsai.presentation.components.PrimaryButton

@Composable
fun SummaryScreen(
    viewModel: SummaryViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val lengthOptions = listOf(
        "short" to "Short (2 Sentences)",
        "detailed" to "Detailed Narrative",
        "key_takeaways" to "Key Takeaways"
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(20.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ShortText, contentDescription = null, tint = SecondaryBlue, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "AI News Summarizer",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Generate concise abstracts and bulleted takeaways using NLP extractive salience ranking.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Summary Length Selector Pills
                Text("Select Summary Length:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    lengthOptions.forEach { (key, label) ->
                        val isSelected = uiState.selectedLength == key
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) SecondaryBlue else MaterialTheme.colorScheme.surface,
                            shadowElevation = if (isSelected) 3.dp else 1.dp,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.onLengthSelected(key) }
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = uiState.inputText,
                    onValueChange = { viewModel.onTextChanged(it) },
                    label = { Text("Paste Article Text") },
                    placeholder = { Text("Paste article content to summarize...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(14.dp)
                )

                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = uiState.errorMessage!!, color = ErrorRed, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(18.dp))

                PrimaryButton(
                    text = if (uiState.isLoading) "Extracting Intelligence..." else "Generate AI Summary",
                    onClick = { viewModel.generateSummary() },
                    enabled = !uiState.isLoading
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Results Presentation
            item {
                uiState.summaryResult?.let { res ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimaryNavy)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("GENERATED SUMMARY", color = AccentCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Surface(
                                    color = SecondaryBlue.copy(alpha = 0.35f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Topic: ${res.mainTopic ?: "General"}",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = res.summary,
                                color = Color.White,
                                fontSize = 14.sp,
                                lineHeight = 22.sp
                            )

                            if (!res.keyTakeaways.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Key Takeaways:", color = AccentCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                res.keyTakeaways.forEach { takeaway ->
                                    Row(modifier = Modifier.padding(vertical = 3.dp)) {
                                        Text("• ", color = AccentCyan, fontWeight = FontWeight.Bold)
                                        Text(takeaway, color = DarkText, fontSize = 12.sp, lineHeight = 18.sp)
                                    }
                                }
                            }

                            if (!res.importantEntities.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Text("Extracted Entities:", color = DarkSecondaryText, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(res.importantEntities) { entity ->
                                        Surface(
                                            color = Color.White.copy(alpha = 0.10f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = entity,
                                                color = DarkText,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
