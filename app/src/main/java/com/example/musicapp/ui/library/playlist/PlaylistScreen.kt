package com.example.musicapp.ui.library.playlist

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicapp.AppViewModelFactory
import com.example.musicapp.domain.model.Playlist
import com.example.musicapp.ui.library.artist.FilterButton
import java.io.File

@Composable
fun PlaylistManagementRoute(
    onPlaylistClick: (Long) -> Unit,
    onAddPlaylist: () -> Unit,
    onEditPlaylist: (Long) -> Unit,
    viewModel: PlaylistManagementViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {

    val uiState by viewModel.uiState.collectAsState()
    val myPlaylistCount by viewModel.myPlaylistCount.collectAsState()

    val isEnabled = viewModel.canCreatePlaylist(myPlaylistCount)

    PlaylistManagementScreen(
        uiState = uiState,
        isEnabled = isEnabled,
        onPlaylistClick = { onPlaylistClick(it.id) },
        onSearchChange = viewModel::onSearchChange,
        onSortChange = viewModel::setSort,
        onAddPlaylist = onAddPlaylist,
        onEditPlaylist = onEditPlaylist,
        onDeletePlaylist = viewModel::onDeleteClick,
        onDismissDelete = viewModel::dismissDeleteDialog,
        onConfirmDelete = viewModel::confirmDelete
    )

}

@Composable
fun PlaylistManagementScreen(
    uiState: PlaylistManagementUiState,
    isEnabled: Boolean,
    onPlaylistClick: (Playlist) -> Unit,
    onSearchChange: (String) -> Unit,
    onSortChange: (PlaylistSortType) -> Unit,
    onAddPlaylist: () -> Unit,
    onEditPlaylist: (Long) -> Unit,
    onDeletePlaylist: (Playlist) -> Unit,
    onDismissDelete: () -> Unit,
    onConfirmDelete: () -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6FA))
    ) {

        if (uiState.showDeleteDialog) {
            item {
                AlertDialog(
                    onDismissRequest = onDismissDelete,
                    confirmButton = {
                        TextButton(onClick = onConfirmDelete) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = onDismissDelete) {
                            Text("Cancel")
                        }
                    },
                    title = { Text("Delete Playlist") },
                    text = {
                        Text("Are you sure you want to delete ${uiState.playlistToDelete?.name}?")
                    }
                )
            }
        }

        item {
            PlaylistHeader(
                searchQuery = uiState.query,
                onSearchChange = onSearchChange,
                onAddPlaylist = onAddPlaylist,
                isEnabled = isEnabled
            )

            Spacer(modifier = Modifier.height(20.dp))

            PlaylistFilterRow(
                selectedSort = uiState.sortType,
                onSortChange = onSortChange
            )

            Spacer(modifier = Modifier.height(20.dp))

            PlaylistList(
                playlists = uiState.playlists,
                onPlaylistClick = onPlaylistClick,
                currentUserId = uiState.currentUserId,
                onEditClick = onEditPlaylist,
                onDeleteClick = onDeletePlaylist
            )
        }
    }
}

@Composable
fun PlaylistHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onAddPlaylist: () -> Unit,
    isEnabled: Boolean
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        placeholder = { Text("Search playlists by name...") },
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

    Spacer(modifier = Modifier.height(16.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(50))
            .shadow(10.dp, RoundedCornerShape(50))
            .background(
                if (isEnabled)
                    Brush.horizontalGradient(
                        listOf(Color(0xFF9D50BB), Color(0xFFE81CFF))
                    )
                else
                    Brush.horizontalGradient(
                        listOf(Color.Gray, Color.LightGray)
                    )
            )
            .clickable(enabled = isEnabled) {
                onAddPlaylist()
            },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isEnabled) Icons.Default.Add
                                else Icons.Default.Block,
                contentDescription = null,
                tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isEnabled) "Add New Playlist"
                        else "Max 10 playlists reached",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PlaylistFilterRow(
    selectedSort: PlaylistSortType,
    onSortChange: (PlaylistSortType) -> Unit
) {

    val activeColor = Color(0xFFFF2EC7)

    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        FilterButton(
            text = "My Playlists",
            selected = selectedSort == PlaylistSortType.MY_PLAYLISTS,
            activeColor = activeColor
        ) {
            onSortChange(PlaylistSortType.MY_PLAYLISTS)
        }

        FilterButton(
            text = "Public Playlists",
            selected = selectedSort == PlaylistSortType.PUBLIC_PLAYLISTS,
            activeColor = activeColor
        ) {
            onSortChange(PlaylistSortType.PUBLIC_PLAYLISTS)
        }
    }
}

@Composable
fun PlaylistList(
    playlists: List<Playlist>,
    onPlaylistClick: (Playlist) -> Unit,
    currentUserId: Long?,
    onEditClick: (Long) -> Unit,
    onDeleteClick: (Playlist) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {

        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    "All Playlists",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("${playlists.size} total", color = Color(0xFF64748B))

                Spacer(modifier = Modifier.weight(1f))
            }

            Divider(color = Color(0xFFE2E8F0))

            playlists.forEach { playlist ->
                PlaylistItem(
                    playlist = playlist,
                    onClick = { onPlaylistClick(playlist) },
                    currentUserId = currentUserId,
                    onEditClick = { onEditClick(playlist.id) },
                    onDeleteClick = { onDeleteClick(playlist) }
                )
                Divider(color = Color(0xFFF1F5F9))
            }
        }
    }
}

@Composable
fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit,
    currentUserId: Long?,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isOwner = playlist.ownerId == currentUserId

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {

        if(playlist.imageUrl != null) {
            AsyncImage(
                model = File(playlist.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0))
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(24.dp)
                    )
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {

            Text(
                playlist.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                playlist.description ?: "",
                color = Color(0xFF64748B)
            )
        }

        if (isOwner) {
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, null, tint = Color(0xFF94A3B8))
            }
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, null, tint = Color(0xFF94A3B8))
            }
        }
    }
}

