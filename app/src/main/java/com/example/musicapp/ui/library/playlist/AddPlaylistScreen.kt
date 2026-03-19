package com.example.musicapp.ui.library.playlist

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicapp.AppViewModelFactory
import com.example.musicapp.domain.model.Song
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.forEach


@Composable
fun AddPlaylistRoute(
    playlistId: Long?,
    onBackClick: () -> Unit,
    onAddSongClick: (Long) -> Unit = {},
    viewModel: AddPlaylistViewModel = viewModel(factory = AppViewModelFactory(
        context = LocalContext.current,
        playlistId = playlistId)
    )
) {

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.saveSuccess) {
        if(uiState.saveSuccess) {
            viewModel.consumeSaveSuccess()
            onBackClick()
        }
    }

    AddPlaylistScreen(
        uiState = uiState,
        playlistId = playlistId,
        onBackClick = onBackClick,
        onAddSongClick = {
            onAddSongClick(playlistId ?: 0)
        },
        onNameChange = viewModel::onNameChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onPublicChange = viewModel::onPublicChange,
        onImageSelected = viewModel::onImageSelected,
        onDeleteSong = viewModel::onDeleteClick,
        onDismissDelete = viewModel::dismissDeleteDialog,
        onConfirmDelete = viewModel::confirmDelete,
        onSaveClick = viewModel::savePlaylist
    )

}

@Composable
fun AddPlaylistScreen(
    uiState: AddPlaylistUiState,
    playlistId: Long?,
    onBackClick: () -> Unit,
    onAddSongClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPublicChange: (Boolean) -> Unit,
    onImageSelected: (String) -> Unit,
    onDeleteSong: (Song) -> Unit,
    onDismissDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
    onSaveClick: () -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    val createdDate = remember(uiState.createdAt) {
        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            .format(Date(uiState.createdAt))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FB))
    ) {

        AddPlaylistTopBar(
            onBackClick,
            uiState.name
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {

                        // ===== NAME =====
                        Column {

                            Text("Playlist's name", fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = uiState.name,
                                onValueChange = onNameChange,
                                placeholder = {
                                    Text("Enter the playlist's full name.")
                                },
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Column {

                            Text("Playlist's description", fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = uiState.description ?: "",
                                onValueChange = onDescriptionChange,
                                placeholder = {
                                    Text("Enter the playlist's description.")
                                },
                                shape = RoundedCornerShape(20),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                            )
                        }

                        Column {

                            Text("Playlist visibility", fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {

                                Icon(
                                    if(uiState.isPublic) Icons.Default.Public
                                    else Icons.Default.Lock,
                                    contentDescription = null,
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    if(uiState.isPublic) "Public Playlist"
                                    else "Private Playlist",
                                    modifier = Modifier.weight(1f)
                                )

                                Switch(
                                    checked = uiState.isPublic,
                                    onCheckedChange = onPublicChange
                                )
                            }

                            Text(
                                if(uiState.isPublic) "Anyone can view this playlist"
                                else "Only u can view this playlist",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        Column {

                            Text("Created at", fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = createdDate,
                                onValueChange = {},
                                enabled = false,
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        if (uiState.showDeleteDialog) {
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

                        Column() {
                            Row {
                                Text("Songs in playlist", fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.weight(1f))
                                Button(
                                    onClick = onAddSongClick,
                                    enabled = playlistId != null,
                                    shape = RoundedCornerShape(50),
                                ) {
                                    Text("Add new song")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            SongList(
                                songs = uiState.songs,
                                onDeleteClick = onDeleteSong
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // ===== IMAGE UPLOAD =====
                        Column {

                            Text("Playlist's photo", fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(8.dp))

                            UploadPlaylistBox(
                                imageUrl = uiState.imageUrl,
                                onImageSelected = onImageSelected
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // ===== BUTTONS =====
                        SaveButton(
                            isSaving = false,
                            onClick = onSaveClick
                        )
                        CancelButton(onBackClick)
                    }
                }
            }
        }
    }
}

@Composable
fun AddPlaylistTopBar(
    onBackClick: () -> Unit,
    playlistTitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
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
            if (playlistTitle.isBlank()) "Add new playlist" else "Edit playlist $playlistTitle",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF0F172A)
        )
    }
}

@Composable
fun SongList(
    songs: List<Song>,
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

            songs.forEach { song ->
                SongItem(
                    song = song,
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
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
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
                "Genre: ${song.genre}",
                color = Color(0xFF64748B)
            )
        }

        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Default.Delete, null, tint = Color(0xFF94A3B8))
        }
    }
}

@Composable
fun UploadPlaylistBox(
    imageUrl: String?,
    onImageSelected: (String) -> Unit
) {
    val context = LocalContext.current

    val gradient = Brush.horizontalGradient(
        listOf(
            Color(0xFF9D50BB),
            Color(0xFFE81CFF)
        )
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->

        uri?.let {

            val fileName = "artist_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)

            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            onImageSelected(file.absolutePath)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .border(
                2.dp,
                Color(0xFF4F46E5).copy(alpha = 0.3f),
                RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            if(imageUrl != null) {
                AsyncImage(
                    model = File(imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )

            } else {

                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4F46E5).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = Color(0xFF9D50BB),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Upload photos", fontWeight = FontWeight.Bold)

                Text(
                    "Drag and drop or click to select a file (JPG, PNG, WEBP)",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    launcher.launch("image/*")
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = ButtonDefaults.ContentPadding,
                modifier = Modifier
                    .background(
                        brush = gradient,
                        shape = RoundedCornerShape(50)
                    )
            ) {
                Text("Select file", color = Color.White)
            }
        }
    }
}

@Composable
fun SaveButton(
    isSaving: Boolean,
    onClick: () -> Unit
) {
    val gradient = Brush.horizontalGradient(
        listOf(
            Color(0xFF9D50BB),
            Color(0xFFE81CFF)
        )
    )

    Button(
        onClick = onClick,
        enabled = !isSaving,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = ButtonDefaults.ContentPadding,
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .background(
                brush = gradient,
                shape = RoundedCornerShape(50)
            ),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (isSaving) "Saving....." else "Save Playlist",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CancelButton(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFE2E8F0)
        )
    ) {
        Text("Cancel")
    }
}

@Preview(showBackground = true)
@Composable
fun AddPlaylistScreenPreview() {

}