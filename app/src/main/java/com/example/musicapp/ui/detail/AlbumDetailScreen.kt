package com.example.musicapp.ui.detail

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicapp.AppViewModelFactory
import com.example.musicapp.R
import com.example.musicapp.domain.model.Song
import com.example.musicapp.ui.components.SongItem
import com.example.musicapp.ui.components.SongItemType
import com.example.musicapp.ui.components.formatDuration
import com.example.musicapp.ui.player.PlayerViewModel

@Composable
fun AlbumDetailRoute(
    albumId: Long,
    onBackClick: () -> Unit,
    playerViewModel: PlayerViewModel,
    viewModel: AlbumDetailViewModel = viewModel(
        factory = AppViewModelFactory(
            context = LocalContext.current,
            albumId = albumId)
    )
) {

    val uiState by viewModel.uiState.collectAsState()
    val playerState by playerViewModel.uiState.collectAsState()

    AlbumDetailScreen(
        uiState = uiState,
        currentSongId = playerState.currentSong?.id,
        onBackClick = onBackClick,
        onSongClick = { song ->
            playerViewModel.playFromQueue(
                songs = uiState.songs,
                startSong = song
            )
        }
    )

}

@Composable
fun AlbumDetailScreen(
    uiState: AlbumDetailUiState,
    currentSongId: Long? = null,
    onBackClick: () -> Unit,
    onSongClick: (Song) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(16.dp)
    ) {

        item {
            AlbumTopBar(onBackClick)
        }

        uiState.album?.let { album ->
            item {
                AlbumHeader(
                    title = album.title,
                    artist = uiState.artist?.name ?: "Unknown",
                    releaseDate = album.releaseDate,
                    imageUrl = album.imageUrl,
                    songsCount = uiState.songs.size
                )
            }
        }

        item {
            Spacer(Modifier.height(20.dp))
            PlayButtonsSection()
            Spacer(Modifier.height(20.dp))
        }

        if (uiState.songs.isEmpty()) {
            item {
                Text(
                    text = "No songs available",
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            itemsIndexed(
                items = uiState.songs,
                key = { _, song -> song.id }
            ) { index, song ->
                SongItem(
                    song = song,
                    artistName = uiState.artist?.name ?: "Unknown",
                    songType = SongItemType.HOME,
                    index = index + 1,
                    isPlaying = song.id == currentSongId,
                    onClick = { onSongClick(song) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumTopBar(
    onBackClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        IconButton(
            onClick = onBackClick
        ) {
            Icon(Icons.Default.ArrowBack, null)
        }

        Text(
            "Album Details",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF0F172A)
        )

        IconButton(
            onClick = {}
        ) {
            Icon(Icons.Default.MoreVert, null)
        }
    }
}

@Composable
fun AlbumHeader(
    title: String,
    artist: String,
    releaseDate: String?,
    imageUrl: String?,
    songsCount: Int
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
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
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            title,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            artist,
            color = Color(0xFF8E24AA),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "${releaseDate ?: ""} ● $songsCount Songs ",
            color = Color.Gray
        )
    }
}

@Composable
fun PlayButtonsSection() {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Button(
                onClick = {},
                modifier = Modifier
                    .weight(1f)
                    .height(55.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFAB47BC)
                )
            ) {

                Icon(Icons.Default.PlayArrow, null)

                Spacer(Modifier.width(6.dp))

                Text(
                    "Play All",
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {},
                modifier = Modifier
                    .weight(1f)
                    .height(55.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8E24AA)
                )
            ) {

                Icon(Icons.Default.Shuffle, null)

                Spacer(Modifier.width(6.dp))

                Text(
                    "Shuffle",
                    fontWeight = FontWeight.Bold
                )
            }

        }

        Spacer(Modifier.width(12.dp))

        IconButton(
            onClick = {},
            modifier = Modifier
                .size(55.dp)
                .background(
                    Color.LightGray.copy(alpha = 0.3f),
                    CircleShape
                )
        ) {
            Icon(
                Icons.Default.FavoriteBorder,
                contentDescription = null
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlbumDetailPreview() {
    AlbumDetailScreen(
        uiState = AlbumDetailUiState(),
        onBackClick = {},
        onSongClick = {}
    )
}