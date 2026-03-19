package com.example.musicapp.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import com.example.musicapp.R

@Composable
fun SplashScreen() {

    val scale = remember { Animatable(0.8f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(true) {
        scale.animateTo(1f, tween(800))
        alpha.animateTo(1f, tween(800))

        delay(4000)
    }

    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF9C27B0),
            Color(0xFFE91E63),
            Color(0xFF3F51B5)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(30.dp)
                    )
                    .border(
                        width = 1.5.dp,
                        color = Color.White.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(30.dp)
                    )
                    .padding(18.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_music),
                    contentDescription = "Logo"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "MusicApp",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "YOUR SOUND. YOUR WAY.",
                fontSize = 18.sp,
                color = Color.White,
                letterSpacing = 2.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}

