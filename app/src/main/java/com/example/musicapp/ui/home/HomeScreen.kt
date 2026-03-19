package com.example.musicapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicapp.AppViewModelFactory
import com.example.musicapp.R
import com.example.musicapp.domain.model.Album
import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.model.Playlist
import com.example.musicapp.domain.model.Song
import com.example.musicapp.ui.components.AlbumItem
import com.example.musicapp.ui.components.ArtistItem
import com.example.musicapp.ui.components.PlaylistItem
import com.example.musicapp.ui.components.SectionHeader
import com.example.musicapp.ui.components.SongItem
import com.example.musicapp.ui.components.SongItemType
import com.example.musicapp.ui.player.PlayerViewModel

@Composable
fun HomeRoute(
    onProfileClick: () -> Unit,
    onPlaylistClick: (Long) -> Unit,
    onArtistClick: (Long) -> Unit,
    onAlbumClick: (Long) -> Unit,
    onSongsClick: () -> Unit,
    onArtistsClick: () -> Unit,
    onAlbumsClick: () -> Unit,
    onPlaylistsClick: () -> Unit,
    playerViewModel: PlayerViewModel,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    val uiState by viewModel.uiState.collectAsState()
    val playerState by playerViewModel.uiState.collectAsState()

    val artists = uiState.artists
    val artistMap = artists.associateBy { it.id }

    HomeScreen(
        uiState = uiState,
        artistMap = artistMap,
        currentSongId = playerState.currentSong?.id,
        onProfileClick = onProfileClick,
        onNotificationClick = {},
        onMoreClick = {},
        onQueryChange = viewModel::onQueryChange,
        onSearchSubmit = viewModel::onSearchSubmit,
        onClearQuery = viewModel::onClearQuery,
        onClickRecent = viewModel::onClickRecent,
        onRemoveRecent = viewModel::removeRecent,
        onClearRecent = viewModel::onClearRecent,
        onSongClick = { song ->
            playerViewModel.playFromQueue(
                songs = uiState.songs,
                startSong = song
            )
        },
        onArtistClick = { onArtistClick(it.id) },
        onPlaylistClick = { onPlaylistClick(it.id) },
        onAlbumClick = { onAlbumClick(it.id) },
        onSongsClick = onSongsClick,
        onArtistsClick = onArtistsClick,
        onAlbumsClick = onAlbumsClick,
        onPlaylistsClick = onPlaylistsClick
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    artistMap: Map<Long, Artist> = emptyMap(),
    currentSongId: Long? = null,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onMoreClick: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    onClearQuery: () -> Unit,
    onClickRecent: (String) -> Unit,
    onRemoveRecent: (String) -> Unit,
    onClearRecent: () -> Unit,
    onSongClick: (Song) -> Unit,
    onArtistClick: (Artist) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onAlbumClick: (Album) -> Unit,
    onSongsClick: () -> Unit,
    onArtistsClick: () -> Unit,
    onAlbumsClick: () -> Unit,
    onPlaylistsClick: () -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            HomeTopBar(
                avatarUrl = uiState.avatarUrl,
                onProfileClick = onProfileClick,
                onNotificationClick = onNotificationClick,
                onMoreClick = onMoreClick,
            )
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                HomeSearchBar(
                    query = uiState.query,
                    onQueryChange = onQueryChange,
                    onSearchSubmit = onSearchSubmit,
                    onClearQuery = onClearQuery,
                )
            }
        }

        if (uiState.query.isBlank() && uiState.recentSearches.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    RecentSearchSection(
                        items = uiState.recentSearches,
                        onClick = onClickRecent,
                        onRemove = onRemoveRecent,
                        onClearAll = onClearRecent,
                    )
                }
            }
        }

        if (uiState.songs.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SongResult(
                        songs = uiState.songs.take(5),
                        artistMap = artistMap,
                        currentSongId = currentSongId,
                        onSongClick = onSongClick,
                        onSongsClick = onSongsClick
                    )
                }
            }
        }

        if (uiState.artists.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ArtistResult(
                        artists = uiState.artists.take(5),
                        onArtistClick = onArtistClick,
                        onArtistsClick = onArtistsClick
                    )
                }
            }
        }

        if (uiState.albums.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    AlbumResult(
                        albums = uiState.albums.take(5),
                        onAlbumClick = onAlbumClick,
                        onAlbumsClick = onAlbumsClick
                    )
                }
            }
        }

        if (uiState.playlists.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    PlaylistResult(
                        playlists = uiState.playlists.take(5),
                        onPlaylistClick = onPlaylistClick,
                        onPlaylistsClick = onPlaylistsClick
                    )
                }
            }
        }

        if (uiState.isSearching && uiState.isEmpty) {
            item {
                Text(
                    text = "No results found",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun HomeTopBar(
    avatarUrl: String? = null,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onMoreClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .shadow(1.dp)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        AsyncImage(
            model = avatarUrl ?: R.drawable.ic_account,
            contentDescription = "Profile",
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .clickable { onProfileClick() },
            contentScale = ContentScale.Crop
        )

        Text(
            text = "MusicApp",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.Black
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
            ) {
                Icon(Icons.Default.Notifications, null)
            }

            Spacer(Modifier.width(8.dp))

            IconButton(
                onClick = onMoreClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
            ) {
                Icon(Icons.Default.MoreVert, null)
            }
        }
    }
}

@Composable
fun HomeSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    onClearQuery: () -> Unit,
) {

    val enabled = query.isNotEmpty()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .border(
                width = 1.5.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(28.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    "Search songs, artists...",
                    color = Color(0xFF9E9E9E)
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFF7E57C2)
                )
            },
            modifier = Modifier.weight(1f),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onSearchSubmit() }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,

                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,


                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        if (enabled) {
            IconButton(onClick = onClearQuery) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    tint = Color.Gray
                )
            }
        }

        IconButton(
            onClick = onSearchSubmit,
            enabled = enabled
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Search",
                tint = if (enabled)
                    Color(0xFF7E57C2)
                else
                    Color.LightGray
            )
        }
    }
}

@Composable
fun RecentSearchSection(
    items: List<String>,
    onClick: (String) -> Unit,
    onRemove: (String) -> Unit,
    onClearAll: () -> Unit
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Recent searches",
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Clear all",
                color = Color(0xFFFF2EC7),
                modifier = Modifier.clickable { onClearAll() }
            )
        }
        Spacer(Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEach {
                SearchChip(
                    text = it,
                    onClick = { onClick(it) },
                    onRemove = { onRemove(it) }
                )
            }
        }
    }
}

@Composable
fun SearchChip(
    text: String,
    onClick: () -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF3F3F3))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.width(6.dp))

        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun SongResult(
    songs: List<Song>,
    artistMap: Map<Long, Artist>,
    currentSongId: Long? = null,
    onSongClick: (Song) -> Unit,
    onSongsClick: () -> Unit
) {

    Column {

        SectionHeader(
            "Trending Songs",
            "View all",
             onSongsClick
        )

        if (songs.isEmpty()) {
            Text(
                text = "No songs available",
                color = Color.Gray,
                modifier = Modifier.padding(16.dp)
            )
        } else {

            songs.forEachIndexed { index, song ->

                val artistName = artistMap[song.artistId]?.name ?: "Unknown"

                SongItem(
                    song = song,
                    artistName = artistName,
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

@Composable
fun ArtistResult(
    artists: List<Artist>,
    onArtistClick: (Artist) -> Unit,
    onArtistsClick: () -> Unit
) {

    Column {

        SectionHeader(
            "Hot Artists",
            "View all",
            onArtistsClick
        )

        Spacer(Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(artists) { artist ->

                ArtistItem(
                    artist = artist,
                    onClick = { onArtistClick(artist) }
                )
            }
        }
    }
}


@Composable
fun AlbumResult(
    albums: List<Album>,
    onAlbumClick: (Album) -> Unit,
    onAlbumsClick: () -> Unit
) {

    Column {

        SectionHeader(
            "New Albums",
            "View all",
            onAlbumsClick
        )

        Spacer(Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(albums) { album ->

                AlbumItem(
                    album = album,
                    onClick = { onAlbumClick(album) }
                )
            }
        }
    }
}

@Composable
fun PlaylistResult(
    playlists: List<Playlist>,
    onPlaylistClick: (Playlist) -> Unit,
    onPlaylistsClick: () -> Unit
) {

    Column {

        SectionHeader(
            "New Playlists",
            "View all",
            onPlaylistsClick
        )

        Spacer(Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(playlists) { playlist ->

                PlaylistItem(
                    playlist = playlist,
                    onClick = { onPlaylistClick(playlist) }
                )
            }
        }
    }
}


