package com.example.musicapp.ui.detail

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicapp.AppViewModelFactory
import com.example.musicapp.R
import com.example.musicapp.data.datasource.LocalSongDataSource
import com.example.musicapp.domain.model.Song
import com.example.musicapp.ui.components.AlbumItem
import com.example.musicapp.ui.components.SectionHeader
import com.example.musicapp.ui.components.SongItem
import com.example.musicapp.ui.components.SongItemType
import com.example.musicapp.ui.player.PlayerViewModel

@Composable
fun ArtistDetailRoute(
    artistId: Long,
    currentUserId: Long,
    onAlbumClick: (Long) -> Unit,
    onBackClick: () -> Unit,
    playerViewModel: PlayerViewModel,
    viewModel: ArtistDetailViewModel =
        viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    val uiState by viewModel.uiState.collectAsState()
    val playerState by playerViewModel.uiState.collectAsState()

    LaunchedEffect(artistId, currentUserId) {
        if(currentUserId > 0) {
            viewModel.loadArtist(artistId, currentUserId)
        }
    }

    ArtistDetailScreen(
        uiState = uiState,
        currentSongId = playerState.currentSong?.id,
        onPlayClick = {
            uiState.songs.firstOrNull()?.let { firstSong ->
                playerViewModel.playFromQueue(
                    songs = uiState.songs,
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
        onAlbumClick = onAlbumClick,
        onFollowClick = viewModel::toggleFollow,
        onBackClick = onBackClick,
        onMoreClick = {}
    )
}

@Composable
fun ArtistDetailScreen(
    uiState: ArtistDetailUiState,
    onAlbumClick: (Long) -> Unit,
    currentSongId: Long? = null,
    onSongClick: (Song) -> Unit,
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit,
    onFollowClick: () -> Unit,
    onPlayClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            ArtistHeader(
                artistName = uiState.artistName,
                artistImageUrl = uiState.artistImageUrl,
                onBackClick = onBackClick,
                onMoreClick = onMoreClick,
                onFollowClick = onFollowClick,
                onPlayClick = onPlayClick,
                isFollowed = uiState.isFollowed
            )
        }

        item {
            Spacer(Modifier.height(16.dp))
            SectionHeader(
                title = "Popular",
                action = "View all",
                onActionClick = {}
            )
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
                    artistName = uiState.artistName,
                    songType = SongItemType.HOME,
                    index = index + 1,
                    isPlaying = song.id == currentSongId,
                    onClick = { onSongClick(song) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
            SectionHeader(
                title = "Albums",
                action = "View all",
                onActionClick = {}
            )
        }

        if(uiState.albums.isEmpty()) {
            item {
                Text(
                    text = "No albums available",
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            items(uiState.albums) { album ->
                AlbumItem(
                    album = album,
                    onClick = { onAlbumClick(album.id) },
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            ArtistInfo(
                artistInfo = uiState.artistInfo ?: "",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun ArtistHeader(
    artistName: String,
    artistImageUrl: String? = null,
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit,
    onFollowClick: () -> Unit,
    onPlayClick: () -> Unit,
    isFollowed: Boolean = false,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
    ) {
        AsyncImage(
            model = artistImageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            placeholder = painterResource(R.drawable.ic_error),
            error = painterResource(R.drawable.ic_error)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.1f),
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            HeaderIconButton(Icons.Default.ArrowBack, onBackClick)
            HeaderIconButton(Icons.Default.MoreVert, onMoreClick)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = Color(0xFFFF2EC7),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "VERIFIED ARTIST",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 18.sp,
                    fontWeight = Bold
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = artistName,
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 40.sp
            )
            Spacer(Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onPlayClick,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF2EC7)
                    ),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "PLAY",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(Modifier.width(16.dp))
                IconButton(
                    onClick = onFollowClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color.Black.copy(alpha = 0.4f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isFollowed)
                            Icons.Default.Favorite
                        else
                            Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFollowed) Color(0xFFFF2EC7) else Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .background(
                Color.Black.copy(alpha = 0.4f),
                CircleShape
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Composable
fun ArtistInfo(
    artistInfo: String,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "About",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF6F6F6))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = artistInfo,
                    color = Color.Black.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    maxLines = if (expanded) Int.MAX_VALUE else 6,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (expanded) "Read less" else "Read more",
                    color = Color(0xFFFF2EC7),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .clickable { expanded = !expanded }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArtistDetailScreenPreview() {
    ArtistDetailScreen(
        uiState = ArtistDetailUiState(
            artistName = "Adele",
            artistImageUrl = "https://upload.wikimedia.org/wikipedia/vi/9/96/Adele_-_25_%28Official_Album_Cover%29.png",
            isFollowed = true,
            songs = LocalSongDataSource.getSongByArtist(1L)
        ),
        currentSongId = null,
        onSongClick = {},
        onBackClick = {},
        onMoreClick = {},
        onFollowClick = {},
        onPlayClick = {},
        onAlbumClick = {}
    )
}