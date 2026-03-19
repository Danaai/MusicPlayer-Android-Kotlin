package com.example.musicapp.ui.library.song

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicapp.AppViewModelFactory
import com.example.musicapp.domain.model.Song
import com.example.musicapp.ui.components.SongItem
import com.example.musicapp.ui.components.SongItemType
import com.example.musicapp.ui.library.artist.FilterButton
import com.example.musicapp.ui.player.PlayerViewModel

@Composable
fun SongsRoute(
    playerViewModel: PlayerViewModel,
    viewModel: SongsViewModel = viewModel(factory = AppViewModelFactory(context = LocalContext.current))
) {

    val uiState by viewModel.uiState.collectAsState()
    val playerState by playerViewModel.uiState.collectAsState()

    SongsScreen(
        uiState = uiState,
        currentSongId = playerState.currentSong?.id,
        onSearch = viewModel::onSearch,
        onSortChange = viewModel::setSort,
        onSongClick = { song ->
            playerViewModel.playFromQueue(
                songs = uiState.songs,
                startSong = song
            )
        },
        onNext = viewModel::nextPage,
        onPrev = viewModel::prevPage,
        onPageClick = viewModel::goToPage
    )
}

@Composable
fun SongsScreen(
    uiState: SongsUiState,
    currentSongId: Long? = null,
    onSearch: (String) -> Unit,
    onSortChange: (SongSortType) -> Unit,
    onSongClick: (Song) -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onPageClick: (Int) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6FA))
    ) {

        SongsSearchBar(
            query = uiState.query,
            onSearch = onSearch
        )

        SongsSortRow(
            selectedSort = uiState.sortType,
            onSortChange = onSortChange
        )

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
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
                        artistName = uiState.artistMap[song.artistId] ?: "Unknown",
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

            item {
                Pagination(
                    currentPage = uiState.currentPage,
                    totalPages = uiState.totalPages,
                    onNext = onNext,
                    onPrev = onPrev,
                    onPageClick = onPageClick
                )
            }
        }
    }
}

@Composable
fun SongsSearchBar(
    query: String,
    onSearch: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onSearch,
        placeholder = { Text("Search songs by name...") },
        leadingIcon = { Icon(Icons.Default.Search, null) },
        shape = RoundedCornerShape(50),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color(0xFF4F46E5),
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        )
    )
}

@Composable
fun SongsSortRow(
    selectedSort: SongSortType,
    onSortChange: (SongSortType) -> Unit
) {

    val activeColor = Color(0xFFFF2EC7)

    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        FilterButton(
            text = "Latest",
            selected = selectedSort == SongSortType.LATEST,
            activeColor = activeColor
        ) {
            onSortChange(SongSortType.LATEST)
        }

        FilterButton(
            text = "Favorites",
            selected = selectedSort == SongSortType.FAVORITES,
            activeColor = activeColor
        ) {
            onSortChange(SongSortType.FAVORITES)
        }
    }
}

@Composable
fun Pagination(
    currentPage: Int,
    totalPages: Int,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onPageClick: (Int) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = onPrev,
            enabled = currentPage > 1
        ) {
            Icon(Icons.Default.ArrowBack,null)
        }

        Spacer(modifier = Modifier.width(8.dp))

        for (i in 1..totalPages) {
            PageButton(
                text = i.toString(),
                selected = i == currentPage,
                onClick = { onPageClick(i) }
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onNext,
            enabled = currentPage < totalPages
        ) {
            Icon(Icons.Default.ArrowForward,null)
        }
    }
}

@Composable
fun PageButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
){

    Box(
        modifier = Modifier
            .padding(horizontal = 6.dp)
            .size(36.dp)
            .clip(CircleShape)
            .background(
                if(selected) Color(0xFF8E24AA) else Color.Transparent
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ){
        Text(text,color= if(selected) Color.White else Color.Black)
    }
}

