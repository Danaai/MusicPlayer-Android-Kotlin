package com.example.musicapp.ui.player

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicapp.domain.model.Song

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueBottomSheet(
    queue: List<Song>,
    currentIndex: Int,
    onSongClick: (Int) -> Unit,
    onMove: (from: Int, to: Int) -> Unit,
    onClose: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onClose
    ) {
        val listState = rememberLazyListState()

        LaunchedEffect(currentIndex) {
            if(currentIndex > 0) {
                listState.animateScrollToItem(
                    index = currentIndex,
                    scrollOffset = -200
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Up Next",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            LazyColumn(state = listState) {
                itemsIndexed(queue, key = { _, song -> song.id }) { index, song ->
                    var dragOffset by remember { mutableStateOf(0f) }
                    var draggingIndex by remember { mutableStateOf(index) }

                    val isCurrent = index == currentIndex

                    val bgColor by animateColorAsState(
                        targetValue = if (isCurrent) Color(0x33FF00FF) else Color.Transparent,
                        label = "bg"
                    )

                    val scale by animateFloatAsState(
                        targetValue = if (isCurrent) 1.02f else 1f,
                        label = "scale"
                    )

                    val alpha by animateFloatAsState(
                        targetValue = if (isCurrent) 1f else 0.7f,
                        label = "alpha"
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                this.alpha = alpha
                            }
                            .background(
                                if(isCurrent) Color(0x22FF00FF) else Color.Transparent,
                                RoundedCornerShape(12.dp)
                            )
                            .pointerInput(song.id) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = {
                                        draggingIndex = index
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragOffset += dragAmount.y

                                        val threshold = 60f

                                        if(dragOffset > threshold && draggingIndex < queue.lastIndex) {
                                            onMove(draggingIndex, draggingIndex + 1)
                                            draggingIndex += 1
                                            dragOffset = 0f
                                        }
                                        if(dragOffset < -threshold && draggingIndex > 0) {
                                            onMove(draggingIndex, draggingIndex - 1)
                                            draggingIndex -= 1
                                            dragOffset = 0f
                                        }
                                    },
                                    onDragEnd = {
                                        dragOffset = 0f
                                    }
                                )
                            }
                            .clickable { onSongClick(index) }
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = song.title,
                            fontWeight = if (isCurrent)
                                FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent)
                                Color.Magenta else Color.Black,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Drag",
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    }
}