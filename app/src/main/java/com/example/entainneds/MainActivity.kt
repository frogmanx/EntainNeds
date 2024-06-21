package com.example.entainneds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.entainneds.ui.RaceSummaryScreen
import com.example.entainneds.ui.RaceSummaryViewModel
import com.example.entainneds.ui.theme.EntainNedsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel: RaceSummaryViewModel by viewModels()
        setContent {
            EntainNedsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RaceSummaryScreen(
                        modifier = Modifier.padding(innerPadding),
                        raceSummaryViewModel = viewModel,
                    )
                }
            }
        }
    }
}
