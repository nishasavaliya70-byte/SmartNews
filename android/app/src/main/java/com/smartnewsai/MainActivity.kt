package com.smartnewsai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.smartnewsai.core.theme.SmartNewsTheme
import com.smartnewsai.data.preferences.PreferenceManager
import com.smartnewsai.presentation.navigation.NavGraph

class MainActivity : ComponentActivity() {

    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = PreferenceManager(applicationContext)

        setContent {
            val userDarkModePref by preferenceManager.isDarkMode.collectAsState(initial = null)
            val isDarkTheme = userDarkModePref ?: isSystemInDarkTheme()

            SmartNewsTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        preferenceManager = preferenceManager
                    )
                }
            }
        }
    }
}
