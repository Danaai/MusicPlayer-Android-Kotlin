package com.example.musicapp.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.musicapp.R
import com.example.musicapp.domain.model.Song

@Composable
fun NowPlayingRoute(
    onBack: () -> Unit = {},
    onArtistClick: (Long) -> Unit,
    playerViewModel: PlayerViewModel
) {
    val uiState by playerViewModel.uiState.collectAsState()
    var showQueue by remember { mutableStateOf(false) }

    NowPlayingScreen(
        uiState = uiState,
        onArtistClick = {
            uiState.currentSong?.artistId?.let { onArtistClick(it) }
        },
        onPlayPauseClick = playerViewModel::togglePlayPause,
        onSeek = playerViewModel::seekTo,
        onShuffleClick = playerViewModel::toggleShuffle,
        onRepeatClick = playerViewModel::toggleRepeat,
        onPreviousClick =  playerViewModel::playPrevious,
        onNextClick = playerViewModel::playNext,
        onQueueClick = { showQueue = true },
        onDownClick = onBack,
        onFavoriteClick = playerViewModel::toggleFavorite
    )

    if(showQueue) {
        QueueBottomSheet(
            queue = uiState.queue,
            currentIndex = uiState.currentIndex,
            onSongClick = {
                playerViewModel.playFromQueueAt(it)
                showQueue = false
            },
            onMove = { from, to ->
                playerViewModel.reorderQueue(from, to)
            },
            onClose = { showQueue = false }
        )
    }
}

@Composable
fun NowPlayingScreen(
    uiState: PlayerUiState,
    onArtistClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onSeek: (Float) -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onRepeatClick: () -> Unit,
    onQueueClick: () -> Unit,
    onDownClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFCE93D8),
                        Color(0xFF6A1B9A),
                        Color(0xFF8E24AA),
                        Color(0xFFCE93D8),
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            NowPlayingHeader(
                queueSize = uiState.queue.size,
                currentIndex = uiState.currentIndex,
                onDownClick = onDownClick,
                onQueueClick = onQueueClick
            )
            Spacer(Modifier.height(24.dp))
            uiState.currentSong?.let { song ->
                SongArtwork(
                    imageUrl = song.imageUrl,
                    modifier = Modifier.aspectRatio(1f)
                )
            } ?: Text("No song playing")
            Spacer(Modifier.height(24.dp))
            SongInfo(
                songName = uiState.currentSong?.title ?: "Unknown Song",
                artistName = uiState.currentArtistName,
                onArtistClick = { onArtistClick() },
                isFavorite = uiState.isFavorite,
                onFavoriteClick = onFavoriteClick
            )
            Spacer(Modifier.height(20.dp))
            ProgressBar(
                progress = uiState.progress,
                current = uiState.currentPositionMs,
                duration = uiState.durationMs,
                onSeek = onSeek
            )
            Spacer(Modifier.height(32.dp))
            PlaybackControls(
                isPlaying = uiState.isPlaying,
                isShuffle = uiState.isShuffle,
                isRepeat = uiState.isRepeat,
                onPlayPauseClick = onPlayPauseClick,
                onPreviousClick = onPreviousClick,
                onNextClick = onNextClick,
                onShuffleClick = onShuffleClick,
                onRepeatClick = onRepeatClick
            )
            Spacer(Modifier.height(24.dp))
            ExtraActions()
        }
    }
}

@Composable
fun NowPlayingHeader(
    queueSize: Int,
    currentIndex: Int,
    onDownClick: () -> Unit,
    onQueueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onDownClick,
            modifier = Modifier.clip(CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))


        Text(
            text = "Now Playing",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.weight(1f))

        val remaining = (queueSize - currentIndex - 1)
            .coerceAtLeast(0)

        BadgedBox(
            badge = {
                if (remaining > 0) {
                    Badge {
                        Text(remaining.toString())
                    }
                }
            }
        ) {
            IconButton(
                onClick = onQueueClick,
                modifier = Modifier.clip(CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.QueueMusic,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun SongArtwork(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(300.dp)
            .clip(RoundedCornerShape(28.dp)),
        error = painterResource(R.drawable.ic_error)
    )
}

@Composable
fun SongInfo(
    songName: String,
    artistName: String,
    onArtistClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    isFavorite: Boolean = true,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column( modifier = Modifier.weight(1f) ) {
            Text(
                text = songName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onArtistClick() }
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = artistName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 1
                )
            }
        }
        IconButton(
            onClick = onFavoriteClick
        ) {
            Icon(
                imageVector = if(isFavorite) Icons.Default.Favorite
                                else Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = if (isFavorite) Color.Red else Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
fun ProgressBar(
    progress: Float,
    onSeek: (Float) -> Unit,
    current: Long,
    duration: Long
) {
    var sliderPosition by remember { mutableStateOf(progress) }

    LaunchedEffect(progress) {
        sliderPosition = progress
    }

    Column {
        Slider(
            value = sliderPosition,
            onValueChange = { newValue ->
                sliderPosition = newValue
            },
            onValueChangeFinished = {
                onSeek(sliderPosition)
            },
            colors = SliderDefaults.colors(
                thumbColor = Color.Magenta,
                activeTrackColor = Color.Magenta
            ),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatTime(current), fontSize = 12.sp, color = Color.White)
            Text(formatTime(duration), fontSize = 12.sp, color = Color.White)
        }
    }
}

@Composable
fun PlaybackControls(
    isPlaying: Boolean,
    isShuffle: Boolean = false,
    isRepeat: Boolean = true,
    onPlayPauseClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onRepeatClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onShuffleClick,
            modifier = Modifier
                .clip(CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = null,
                tint = if(isShuffle) Color.Magenta else Color.Gray
            )
        }
        IconButton(
            onClick = onPreviousClick  ,
            modifier = Modifier
                .clip(CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
        }
        FloatingActionButton(
            onClick = onPlayPauseClick,
            containerColor = Color.Magenta,
            modifier = Modifier
                .clip(CircleShape)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .padding(12.dp)
                    .size(64.dp)
            )
        }
        IconButton(
            onClick = onNextClick,
            modifier = Modifier
                .clip(CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
        }
        IconButton(
            onClick = onRepeatClick,
            modifier = Modifier
                .clip(CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Repeat,
                contentDescription = null,
                tint = if(isRepeat) Color.Magenta else Color.Gray
            )
        }
    }
}

@Composable
fun ExtraActions() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AssistChip(
            onClick = {},
            label = { Row(modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 2.dp)) { Text("Song Lyric") } },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lyrics,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(vertical = 12.dp, horizontal = 2.dp)
                )
            },
        )
    }
}

fun formatTime(ms: Long): String {
    val totalSeconds = ms /1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

@Preview(showBackground = true)
@Composable
fun NowPlayingScreenPreview() {
    NowPlayingScreen(
        uiState = PlayerUiState(
            currentSong = Song(
                id = 1L,
                title = "Easy On Me",
                imageUrl = null,
                releaseDate = "2023",
                durationMs = 210000,
                artistId = 1L,
                audioUrl = ""
            )
        ),
        onPlayPauseClick = {},
        onSeek = {},
        onPreviousClick = {},
        onNextClick = {},
        onShuffleClick = {},
        onRepeatClick = {},
        onQueueClick = {},
        onDownClick = {},
        onFavoriteClick = {},
        onArtistClick = {}
    )
}