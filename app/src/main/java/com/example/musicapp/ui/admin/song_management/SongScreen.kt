package com.example.musicapp.ui.admin.song_management

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicapp.AppViewModelFactory
import com.example.musicapp.domain.model.Song
import java.io.File

@Composable
fun SongManagementRoute(
    onBackClick: () -> Unit,
    onAddSong: () -> Unit,
    onEditSong: (Long) -> Unit,
    viewModel: SongManagementViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {

    val uiState by viewModel.uiState.collectAsState()

    SongManagementScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onSearchChange = viewModel::onSearchChange,
        onAddSong = onAddSong,
        onEditSong = onEditSong,
        onDeleteClick = viewModel::onDeleteClick,
        onDismissDelete = viewModel::dismissDeleteDialog,
        onConfirmDelete = viewModel::confirmDelete,
        onNext = viewModel::nextPage,
        onPrev = viewModel::prevPage,
        onPageClick = viewModel::goToPage
    )

}

@Composable
fun SongManagementScreen(
    uiState: SongManagementUiState,
    onBackClick: () -> Unit,
    onSearchChange: (String) -> Unit,
    onAddSong: () -> Unit,
    onEditSong: (Long) -> Unit,
    onDeleteClick: (Song) -> Unit,
    onDismissDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onPageClick: (Int) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FB)),
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
                    title = { Text("Delete Song") },
                    text = {
                        Text("Are you sure you want to delete ${uiState.songToDelete?.title}?")
                    }
                )
            }
        }

        item {
            SongTopBar(onBackClick)

            Spacer(modifier = Modifier.height(16.dp))

            SongHeader(
                searchQuery = uiState.searchQuery,
                onSearchChange = onSearchChange,
                onAddSong = onAddSong
            )

            Spacer(modifier = Modifier.height(16.dp))

            SongStats()

            Spacer(modifier = Modifier.height(20.dp))

            SongList(
                songs = uiState.songs,
                onEditClick = onEditSong,
                onDeleteClick = onDeleteClick
            )

            Pagination(
                currentPage = uiState.currentPage,
                totalPages = uiState.totalPages,
                onNext = onNext,
                onPrev = onPrev,
                onPageClick = onPageClick
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun SongTopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = onBackClick
        ) {
            Icon(Icons.Default.ArrowBack, null)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            "Manage Songs",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF0F172A)
        )
    }
}

@Composable
fun SongHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onAddSong: () -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchChange,
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

    Spacer(modifier = Modifier.height(16.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(50))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF9D50BB), Color(0xFFE81CFF))
                )
            )
            .shadow(10.dp, RoundedCornerShape(50))
            .clickable { onAddSong() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Add, null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Add New Song",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SongStats() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard("NUMBER OF SONGS", "???", Modifier.weight(1f))
            StatCard("NUMBER OF LISTENS", "???", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard("ARTISTS", "???", Modifier.weight(1f))
            StatCard("CAPACITY", "???", Modifier.weight(1f))
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(20.dp)
    ) {
        Text(title, color = Color(0xFF64748B), fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(value, color = Color(0xFF6366F1), fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SongList(
    songs: List<Pair<Song, String>>,
    onEditClick: (Long) -> Unit,
    onDeleteClick: (Song) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    "All Songs",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("${songs.size} total", color = Color(0xFF64748B))

                Spacer(modifier = Modifier.weight(1f))
            }

            Divider(color = Color(0xFFE2E8F0))

            songs.forEach { (song, artistName) ->
                SongItem(
                    song = song,
                    artistName = artistName,
                    onEditClick = { onEditClick(song.id) },
                    onDeleteClick = { onDeleteClick(song) }
                )
                Divider(color = Color(0xFFF1F5F9))
            }
        }
    }
}

@Composable
fun SongItem(
    song: Song,
    artistName: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if(song.imageUrl != null) {
            AsyncImage(
                model = File(song.imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(63.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE2E8F0))
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {

            Text(
                song.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = artistName,
                color = Color(0xFF64748B)
            )
        }

        IconButton(onClick = onEditClick) {
            Icon(Icons.Default.Edit, null, tint = Color(0xFF94A3B8))
        }
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Default.Delete, null, tint = Color(0xFF94A3B8))
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