package com.example.musicapp.ui.admin.album_manangement

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
import com.example.musicapp.domain.model.Album
import java.io.File
import kotlin.collections.forEach

@Composable
fun AlbumManagementRoute(
    onBackClick: () -> Unit,
    onAddAlbum: () -> Unit,
    onEditAlbum: (Long) -> Unit,
    viewModel: AlbumManagementViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {

    val uiState by viewModel.uiState.collectAsState()

    AlbumManagementScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onSearchChange = viewModel::onSearchChange,
        onAddAlbum = onAddAlbum,
        onEditAlbum = onEditAlbum,
        onDeleteAlbum = viewModel::onDeleteClick,
        onDismissDelete = viewModel::dismissDeleteDialog,
        onConfirmDelete = viewModel::confirmDelete,
        onNext = viewModel::nextPage,
        onPrev = viewModel::prevPage,
        onPageClick = viewModel::goToPage
    )

}

@Composable
fun AlbumManagementScreen(
    uiState: AlbumManagementUiState,
    onBackClick: () -> Unit,
    onSearchChange: (String) -> Unit,
    onAddAlbum: () -> Unit,
    onEditAlbum: (Long) -> Unit,
    onDeleteAlbum: (Album) -> Unit,
    onDismissDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onPageClick: (Int) -> Unit
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
                    title = { Text("Delete Album") },
                    text = {
                        Text("Are you sure you want to delete ${uiState.albumToDelete?.title}?")
                    }
                )
            }
        }

        item {
            AlbumTopBar(onBackClick)

            Spacer(modifier = Modifier.height(16.dp))

            AlbumHeader(
                searchQuery = uiState.searchQuery,
                onSearchChange = onSearchChange,
                onAddAlbum = onAddAlbum
            )

            Spacer(modifier = Modifier.height(20.dp))

            AlbumList(
                albums = uiState.albums,
                onEditClick = onEditAlbum,
                onDeleteClick = onDeleteAlbum
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
fun AlbumTopBar(
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
            "Manage Albums",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF0F172A)
        )
    }
}

@Composable
fun AlbumHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onAddAlbum: () -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        placeholder = { Text("Search albums by name...") },
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
            .clickable { onAddAlbum() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Add, null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Add New Album",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AlbumList(
    albums: List<Pair<Album, String>>,
    onEditClick: (Long) -> Unit,
    onDeleteClick: (Album) -> Unit
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
                    "All Albums",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("${albums.size} total", color = Color(0xFF64748B))

                Spacer(modifier = Modifier.weight(1f))
            }

            Divider(color = Color(0xFFE2E8F0))

            albums.forEach { (album, artistName) ->
                AlbumItem(
                    album = album,
                    artistName = artistName,
                    onEditClick = { onEditClick(album.id) },
                    onDeleteClick = { onDeleteClick(album) }
                )
                Divider(color = Color(0xFFF1F5F9))
            }
        }
    }
}

@Composable
fun AlbumItem(
    album: Album,
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

        if(album.imageUrl != null) {
            AsyncImage(
                model = File(album.imageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        } else {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0))
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = album.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = artistName,
                color = Color.Black,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "${album.releaseDate}",
                color = Color.Black.copy(alpha = 0.6f)
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
    text:String,
    selected:Boolean,
    onClick: () -> Unit
){

    Box(
        modifier = Modifier
            .padding(horizontal = 6.dp)
            .size(36.dp)
            .clip(CircleShape)
            .background(
                if (selected) Color(0xFF8E24AA) else Color.Transparent
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ){
        Text(text,color= if(selected) Color.White else Color.Black)
    }
}