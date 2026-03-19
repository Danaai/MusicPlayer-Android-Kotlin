package com.example.musicapp.ui.admin.album_manangement

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicapp.AppViewModelFactory
import com.example.musicapp.domain.model.Song

@Composable
fun AddSongToAlbumRoute(
    albumId: Long,
    onBackClick: () -> Unit,
    viewModel: AddSongToAlbumViewModel = viewModel(
        factory = AppViewModelFactory(
            context = LocalContext.current,
            albumId = albumId
        )
    )
) {

    val uiState by viewModel.uiState.collectAsState()

    AddSongToAlbumScreen(
        songs = uiState.songs,
        selectedSongs = uiState.selectedSongs,
        searchQuery = uiState.searchQuery,
        onSearchChange = viewModel::onSearchChange,
        onToggleSong = viewModel::toggleSong,
        onSaveClick = {
            viewModel.saveSongs()
            onBackClick()
        },
        onBackClick = onBackClick
    )
}

@Composable
fun AddSongToAlbumScreen(
    songs: List<Song>,
    selectedSongs: Set<Long>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onToggleSong: (Long) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, null)
            }

            Text(
                "Add song to album",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search song...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {

            items(songs) { song ->

                SongSelectableItem(
                    song = song,
                    isSelected = selectedSongs.contains(song.id),
                    onClick = { onToggleSong(song.id) }
                )

            }

        }

        Button(
            onClick = onSaveClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Text("Save (${selectedSongs.size})")

        }

    }
}

@Composable
fun SongSelectableItem(
    song: Song,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        RadioButton(
            selected = isSelected,
            onClick = onClick
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {

            Text(
                song.title,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                "Artist ID: ${song.artistId}",
                fontSize = 12.sp
            )
        }

    }
}