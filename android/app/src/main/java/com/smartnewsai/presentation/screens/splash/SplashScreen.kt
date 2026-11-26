package com.smartnewsai.presentation.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartnewsai.R
import com.smartnewsai.core.theme.AccentCyan
import com.smartnewsai.core.theme.DarkBackground
import com.smartnewsai.core.theme.PrimaryNavy
import com.smartnewsai.core.theme.SecondaryBlue

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigate: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is SplashUiState.Navigate) {
            val destination = (uiState as SplashUiState.Navigate).route
            onNavigate(destination)
        }
    }

    // Introductory animations: Scale + Fade
    val transition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "alphaAnim"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryNavy,
                        DarkBackground
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(32.dp)
                .alpha(alphaAnim)
        ) {
            // App Logo
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(130.dp)
                    .scale(pulseScale)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_smartnews_logo),
                    contentDescription = "SmartNews AI Logo",
                    modifier = Modifier.size(110.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Application Title
            Text(
                text = "SMARTNEWS AI",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle Tagline
            Text(
                text = "Next-Gen Editorial & NLP Insights",
                color = AccentCyan,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Material 3 Loading Indicator
            CircularProgressIndicator(
                modifier = Modifier.size(36.dp),
                color = SecondaryBlue,
                strokeWidth = 3.dp
            )
        }

        // Bottom academic / version footer
        Text(
            text = "SmartNews AI v1.0 • Academic Edition",
            color = Color.White.copy(alpha = 0.5f),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}
