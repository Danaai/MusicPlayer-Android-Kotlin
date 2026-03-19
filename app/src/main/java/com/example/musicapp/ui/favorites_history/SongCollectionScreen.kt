package com.example.musicapp.ui.favorites_history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicapp.AppViewModelFactory
import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.model.Song
import com.example.musicapp.ui.components.SongItem
import com.example.musicapp.ui.components.SongItemType
import com.example.musicapp.ui.player.PlayerViewModel

@Composable
fun SongCollectionRoute(
    type: SongCollectionType,
    onBackClick: () -> Unit,
    playerViewModel: PlayerViewModel,
    viewModel: SongCollectionViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    val uiState by viewModel.uiState.collectAsState()
    val playState by playerViewModel.uiState.collectAsState()

    val artistMap = uiState.artists.associateBy { it.id }

    LaunchedEffect(type) {
        viewModel.loadSongs(type)
    }

    SongCollectionScreen(
        uiState = uiState,
        artistMap = artistMap,
        currentSongId = playState.currentSong?.id,
        onBackClick = onBackClick,
        onPlayAllClick = {
            val songs = viewModel.playAll()
            if(songs.isNotEmpty()) {
                playerViewModel.playFromQueue(
                    songs = songs,
                    startSong = songs.first()
                )
            }
        },
        onShuffleClick = viewModel::shuffle,
        onSongClick = { song ->
            playerViewModel.playFromQueue(
                songs = uiState.songs,
                startSong = song
            )
        },
        onMoreClick = {}
    )
}

@Composable
fun SongCollectionScreen(
    uiState: SongCollectionUiState,
    artistMap: Map<Long, Artist> = emptyMap(),
    currentSongId: Long? = null,
    onBackClick: () -> Unit,
    onPlayAllClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onSongClick: (Song) -> Unit,
    onMoreClick: (Song) -> Unit
) {
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading...")
        }
        return
    }

    uiState.errorMessage?.let {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = it,
                color = Color.Red
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            SongCollectionTopbar(
                title = uiState.title,
                onBackClick = onBackClick
            )
        }

        item {
            if (!uiState.isLoading && uiState.songs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No songs yet",
                        color = Color.Gray
                    )
                }
            }
        }

        item {
            SongCollectionHeader(
                title = uiState.title,
                songCount = uiState.songs.size,
                isShuffled = uiState.isShuffled,
                onPlayAllClick = onPlayAllClick,
                onShuffleClick = onShuffleClick
            )
        }

        itemsIndexed(
            items = uiState.songs,
            key = {_, song -> song.id}
        ) { index, song ->
            val artistName = artistMap[song.artistId]?.name ?: "Unknown"

            SongItem(
                song = song,
                artistName = artistName,
                songType = SongItemType.HOME,
                index = index + 1,
                isPlaying = song.id == currentSongId,
                onClick = { onSongClick(song) },
                onMoreClick = { onMoreClick(song) },
            )
        }
    }
}

@Composable
fun SongCollectionTopbar(
    title: String,
    onBackClick: () -> Unit
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
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun SongCollectionHeader(
    title: String,
    songCount: Int,
    isShuffled: Boolean = false,
    onPlayAllClick: () -> Unit,
    onShuffleClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(
                    brush = Brush.linearGradient(
                        listOf(Color(0xFF2A0A3D), Color(0xFFB15C9B))
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
        )

        Spacer(Modifier.height(16.dp))

        Text(title, fontSize = 26.sp)
        Text("$songCount songs", color = Color(0xFF7A3EF0))

        Spacer(Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            Button(
                onClick = onPlayAllClick,
                enabled = songCount > 0,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8E3AF2)
                ),
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Play All",
                    color = Color.White
                )
            }

            Spacer(Modifier.width(12.dp))

            Card(
                modifier = Modifier
                    .size(52.dp)
                    .clickable { onShuffleClick() },
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF1EAFE)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = null,
                        tint = if (isShuffled)
                            Color(0xFF8E3AF2)
                        else
                            Color.Gray
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SongCollectionPreview() {
    SongCollectionScreen(
        uiState = SongCollectionUiState(
            title = "Playlist Name",
            songs = emptyList(),
        ),
        currentSongId = null,
        onBackClick = {},
        onPlayAllClick = {},
        onShuffleClick = {},
        onSongClick = {},
        onMoreClick = {}
    )
}