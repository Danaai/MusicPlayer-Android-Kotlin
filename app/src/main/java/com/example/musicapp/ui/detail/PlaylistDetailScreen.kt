package com.example.musicapp.ui.detail

import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicapp.AppViewModelFactory
import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.model.Song
import com.example.musicapp.ui.components.SongItem
import com.example.musicapp.ui.components.SongItemType
import com.example.musicapp.ui.player.PlayerViewModel

@Composable
fun PlaylistDetailRoute(
    playlistId: Long,
    playerViewModel: PlayerViewModel,
    onBackClick: () -> Unit,
    viewModel: PlaylistDetailViewModel =
        viewModel(factory = AppViewModelFactory(LocalContext.current))
) {

    val uiState by viewModel.uiState.collectAsState()
    val playerState by playerViewModel.uiState.collectAsState()

    LaunchedEffect(playlistId) {
        viewModel.loadPlaylist(playlistId)
    }

    PlaylistDetailScreen(
        playlistName = uiState.playlistName,
        ownerName = uiState.ownerName,
        playlistImageUrl = uiState.playlistImageUrl,
        songs = uiState.songs,
        artistMap = uiState.artists.associateBy { it.id },
        currentSongId = playerState.currentSong?.id,
        onBackClick = onBackClick,
        onPlayClick = {
            uiState.songs.firstOrNull()?.let { firstSong ->
                playerViewModel.playFromQueue(
                    songs = uiState.songs,
                    startSong = firstSong
                )
            }
        },
        onShuffleClick = {
            uiState.songs.shuffled().firstOrNull()?.let { firstSong ->
                playerViewModel.playFromQueue(
                    songs = uiState.songs.shuffled(),
                    startSong = firstSong
                )
            }
        },
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
fun PlaylistDetailScreen(
    playlistName: String,
    ownerName: String,
    playlistImageUrl: String? = null,
    songs: List<Song> = emptyList(),
    artistMap: Map<Long, Artist> = emptyMap(),
    currentSongId: Long? = null,
    onBackClick: () -> Unit,
    onPlayClick: () -> Unit,
    onShuffleClick: () -> Unit,
    onSongClick: (Song) -> Unit,
    onMoreClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        PlaylistHeader(
            playlistName = playlistName,
            onBackClick = onBackClick
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                PlaylistArtwork(
                    imageUrl = playlistImageUrl
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                PlaylistInfo(
                    playlistName = playlistName,
                    ownerName = ownerName,
                    numberOfSongs = songs.size,
                    totalDuration = formatTotalDuration(songs)
                )
            }
            item {
                Spacer(modifier = Modifier.height(20.dp))
                PlaylistActions(
                    onPlayClick = onPlayClick,
                    onShuffleClick = onShuffleClick
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            itemsIndexed(
                items = songs,
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun PlaylistHeader(
    playlistName: String,
    onBackClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clip(CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null
            )
        }
        Text(
            text = playlistName,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PlaylistArtwork(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .size(280.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.LightGray)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun PlaylistInfo(
    playlistName: String,
    ownerName: String,
    numberOfSongs: Int,
    totalDuration: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = playlistName,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
        )
        Text(
            text = ownerName,
            fontSize = 14.sp,
            color = Color.Magenta
        )
        Text(
            text = "$numberOfSongs songs • $totalDuration",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun PlaylistActions(
    onPlayClick: () -> Unit,
    onShuffleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onPlayClick,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Magenta
            ),
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFFFF4D8D), Color(0xFFFF77A9))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Play All")
        }
        OutlinedButton(
            onClick = onShuffleClick,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = null,
                tint = Color.Magenta
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Shuffle", color = Color.Magenta)
        }
    }
}

fun formatTotalDuration(songs: List<Song>): String {
    val totalMs = songs.sumOf { it.durationMs }
    val totalMinutes = totalMs / 1000 / 60
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return if (hours > 0 ) {
        "${hours}h ${minutes}m"
    } else {
        "${minutes}m"
    }
}

@Preview(showBackground = true)
@Composable
fun PlaylistDetailPreview() {
    PlaylistDetailScreen(
        playlistName = "Playlist Name",
        ownerName = "Owner Name",
        onBackClick = {},
        onPlayClick = {},
        onShuffleClick = {},
        onSongClick = {},
        onMoreClick = {}
    )
}