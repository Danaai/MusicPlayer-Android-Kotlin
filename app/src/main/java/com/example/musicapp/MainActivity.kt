package com.example.musicapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.musicapp.navigation.RootNavGraph
import com.example.musicapp.ui.auth.AuthViewModel
import com.example.musicapp.ui.theme.MusicAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicAppTheme {

                val navController = rememberNavController()

                val factory = AppViewModelFactory(this)

                val authViewModel: AuthViewModel = viewModel(
                    factory = factory
                )

                val authState by authViewModel.authState.collectAsState()

                RootNavGraph(
                    navController = navController,
                    authState = authState,
                    factory = factory
                )
            }
        }
    }
}