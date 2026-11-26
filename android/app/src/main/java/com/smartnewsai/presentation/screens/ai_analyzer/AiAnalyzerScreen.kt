package com.smartnewsai.presentation.screens.ai_analyzer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
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
import com.smartnewsai.core.theme.*
import com.smartnewsai.presentation.components.PrimaryButton

@Composable
fun AiAnalyzerScreen(
    viewModel: AiAnalyzerViewModel,
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
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = SecondaryBlue,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "AI News Analyzer",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryNavy
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Paste text, headlines, or URLs to evaluate category classification & credibility signals.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    // Headline Input Field
                    OutlinedTextField(
                        value = uiState.headlineInput,
                        onValueChange = { viewModel.onHeadlineChanged(it) },
                        label = { Text("Headline / Title") },
                        placeholder = { Text("e.g. Breakthrough in nuclear fusion...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Body Text Input Field
                    OutlinedTextField(
                        value = uiState.articleTextInput,
                        onValueChange = { viewModel.onArticleTextChanged(it) },
                        label = { Text("Article Text / Excerpt") },
                        placeholder = { Text("Paste story paragraphs here for NLP evaluation...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // URL Input Field
                    OutlinedTextField(
                        value = uiState.urlInput,
                        onValueChange = { viewModel.onUrlChanged(it) },
                        label = { Text("Article URL (Optional)") },
                        placeholder = { Text("https://example.com/news/123") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    if (uiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.errorMessage!!,
                            color = ErrorRed,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    PrimaryButton(
                        text = if (uiState.isAnalyzing) "Analyzing NLP Signals..." else "Run Deep AI Analysis",
                        onClick = { viewModel.runAnalysis() },
                        enabled = !uiState.isAnalyzing
                    )
                }
            }

            // Analysis Result Presentation
            item {
                uiState.result?.let { result ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimaryNavy)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "EVALUATION SUMMARY",
                                    color = AccentCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = result.modelVersion,
                                    color = DarkSecondaryText,
                                    fontSize = 11.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Category prediction pill
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Predicted Category: ", color = DarkText, fontSize = 13.sp)
                                Surface(
                                    color = SecondaryBlue,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = result.predictedCategory,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Credibility Score Progress
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Credibility Indicator", color = DarkText, fontSize = 13.sp)
                                Text(
                                    text = "${(result.credibilityScore * 100).toInt()}% (${result.credibilityLabel})",
                                    color = if (result.credibilityScore > 0.7f) AccentCyan else WarningAmber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { result.credibilityScore },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = if (result.credibilityScore > 0.7f) AccentCyan else WarningAmber,
                                trackColor = Color.White.copy(alpha = 0.15f)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Signals detected
                            Text("Linguistic Signals Detected:", color = AccentCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            result.suspiciousLanguageSignals.forEach { signal ->
                                Row(modifier = Modifier.padding(vertical = 3.dp)) {
                                    Icon(
                                        imageVector = if (result.credibilityScore > 0.7f) Icons.Default.Verified else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = if (result.credibilityScore > 0.7f) AccentCyan else WarningAmber,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(signal, color = DarkText, fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Text("Model Explanation:", color = AccentCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(result.modelExplanation, color = DarkText, fontSize = 12.sp, lineHeight = 18.sp)

                            Spacer(modifier = Modifier.height(16.dp))

                            // Ethical Academic Disclaimer
                            Surface(
                                color = Color.White.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = AccentCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Academic Notice: A machine learning model score is a probabilistic assessment of linguistic style, not proof of factual truth. Independent verification is always recommended.",
                                        color = DarkSecondaryText,
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp
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
