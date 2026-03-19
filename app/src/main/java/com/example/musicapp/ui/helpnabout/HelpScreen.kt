package com.example.musicapp.ui.helpnabout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicapp.R

@Composable
fun HelpRoute(
    onBackClick: () -> Unit,
    onChatClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    HelpScreen(
        onBackClick = onBackClick,
        onChatClick = onChatClick,
        onInfoClick = onInfoClick
    )
}

@Composable
fun HelpScreen(
    onBackClick: () -> Unit,
    onChatClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    val faqs = remember {
        listOf(
            R.string.question_1 to R.string.answer_1,
            R.string.question_2 to R.string.answer_2,
            R.string.question_3 to R.string.answer_3,
            R.string.question_4 to R.string.answer_4,
            R.string.question_5 to R.string.answer_5
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6FA))
    ) {
        HelpTopBar(
            onBackClick = onBackClick,
            onInfoClick = onInfoClick
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(faqs) { (questionId, answerId) ->
                FaqItemCard(
                    questionId = questionId,
                    answerId = answerId
                )
            }

            item {
                Spacer(Modifier.height(24.dp))
                ChatSupportButton(onChatClick = onChatClick)
            }
        }
    }
}

@Composable
fun HelpTopBar(
    onBackClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null
            )
        }

        Text(
            text = "Frequently asked questions",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )

        IconButton(
            onClick = onInfoClick,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
            )
        }
    }
}

@Composable
fun FaqItemCard(
    questionId: Int,
    answerId: Int
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(questionId),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded)
                        Icons.Default.KeyboardArrowUp
                    else
                        Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = expanded) {
                Text(
                    text = stringResource(answerId),
                    fontSize = 14.sp,
                    color = Color(0xFF4A4A4A),
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}

@Composable
fun ChatSupportButton(
    onChatClick: () -> Unit
) {
    Button(
        onClick = onChatClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1E2AEF)
        )
    ) {
        Icon(
            imageVector = Icons.Default.Chat,
            contentDescription = null,
            tint = Color.White
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Chat with support",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HelpPreview() {
    HelpScreen(
        onBackClick = {},
        onChatClick = {},
        onInfoClick = {}
    )
}
