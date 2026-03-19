package com.example.musicapp.ui.admin.song_management

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicapp.AppViewModelFactory
import com.example.musicapp.domain.model.Artist

@Composable
fun AddSongRoute(
    songId: Long?,
    onBackClick: () -> Unit,
    viewModel: AddSongViewModel = viewModel(factory = AppViewModelFactory(
        context = LocalContext.current,
        songId = songId)
    )
) {

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.saveSuccess) {
        if(uiState.saveSuccess) {
            viewModel.consumeSaveSuccess()
            onBackClick()
        }
    }

    AddSongScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onTitleChange = viewModel::onTitleChange,
        onArtistQueryChange = viewModel::onArtistQueryChange,
        onArtistSelected = viewModel::onArtistSelected,
        onGenreChange = viewModel::onGenreChange,
        onReleaseDateChange = viewModel::onReleaseDateChange,
        onPreviewClick = viewModel::playPreview,
        onStopClick = viewModel::stopPreview,
        onSeek = viewModel::seekTo,
        onAudioSelected = viewModel::onAudioSelected,
        onImageSelected = viewModel::onImageSelected,
        onSaveClick = viewModel::saveSong
    )

}

@Composable
fun AddSongScreen(
    uiState: AddSongUiState,
    onBackClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onArtistQueryChange: (String) -> Unit,
    onArtistSelected: (Artist) -> Unit,
    onGenreChange: (String) -> Unit,
    onReleaseDateChange: (String) -> Unit,
    onPreviewClick: () -> Unit,
    onStopClick: () -> Unit,
    onSeek: (Long) -> Unit,
    onAudioSelected: (Uri) -> Unit,
    onImageSelected: (Uri) -> Unit,
    onSaveClick: () -> Unit,
) {

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FB))
    ) {

        AddSongTopBar(
            onBackClick,
            uiState.title
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            item {
                SectionTitle("Basic information", Icons.Default.Info)
            }

            item {
                RoundedTextField(
                    label = "Song name",
                    value = uiState.title,
                    placeholder = "Ex: 7 years",
                    onValueChange = onTitleChange
                )
            }

            item {
                ArtistAutocomplete(
                    query = uiState.artistQuery,
                    suggestions = uiState.artistSuggestions,
                    onQueryChange = onArtistQueryChange,
                    onArtistSelected = onArtistSelected
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                    RoundedTextField(
                        label = "Duration",
                        value = if(uiState.duration > 0)
                            "${formatDuration(uiState.duration)}"
                        else
                            "Not selected",
                        placeholder = "04:20",
                        onValueChange = {},
                        enabled = false,
                        modifier = Modifier.weight(1f)
                    )

                    RoundedTextField(
                        label = "Genre",
                        value = uiState.genre,
                        placeholder = "Ex: Pop, Rock,....",
                        onValueChange = onGenreChange,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                RoundedTextField(
                    label = "Release time",
                    value = uiState.releaseDate,
                    placeholder = "Ex: 19-04-2015",
                    onValueChange = onReleaseDateChange
                )
            }

            // ===== FILE UPLOAD =====
            item {
                SectionTitle("Files and Images", Icons.Default.CloudUpload)
            }

            item {
                UploadAudioBox(
                    audioPath = uiState.audioPath,
                    duration = uiState.duration,
                    currentPosition = uiState.currentPosition,
                    onPreviewClick = onPreviewClick,
                    onStopClick = onStopClick,
                    onSeek = onSeek,
                    onAudioSelected = onAudioSelected
                )
            }

            item {
                UploadImageBox(
                    uiState.imagePath,
                    onImageSelected = onImageSelected
                )
            }

            // ===== BUTTONS =====
            item {
                Spacer(modifier = Modifier.height(12.dp))

                SaveButton(
                    isSaving = uiState.isSaving,
                    onClick = onSaveClick
                )

                Spacer(modifier = Modifier.height(12.dp))

                CancelButton(onBackClick)
            }
        }
    }
}

@Composable
fun AddSongTopBar(
    onBackClick: () -> Unit,
    songTitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onBackClick) {
            Icon(Icons.Default.ArrowBack, contentDescription = null)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            if (songTitle.isBlank()) "Add new song" else "Edit song $songTitle",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF0F172A)
        )
    }
}

@Composable
fun SectionTitle(text: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color(0xFF9D50BB))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

@Composable
fun RoundedTextField(
    label: String,
    value: String,
    enabled: Boolean = true,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        Text(label, fontWeight = FontWeight.Medium)

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            enabled = enabled,
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedBorderColor = Color(0xFF6366F1),
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistAutocomplete(
    query: String,
    suggestions: List<Artist>,
    onQueryChange: (String) -> Unit,
    onArtistSelected: (Artist) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded && suggestions.isNotEmpty(),
        onExpandedChange = { expanded = !expanded}
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                onQueryChange(it)
                expanded = true
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            placeholder = { Text("Enter artist name or Id") },
            shape = RoundedCornerShape(50),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedBorderColor = Color(0xFF9D50BB),
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        ExposedDropdownMenu(
            expanded = expanded && suggestions.isNotEmpty(),
            onDismissRequest = { expanded = false }
        ) {
            suggestions.forEach { artist ->
                DropdownMenuItem(
                    text = { Text("${artist.id} - ${artist.name}") },
                    onClick = {
                        onArtistSelected(artist)
                        expanded = false
                    }
                )
            }
        }
    }

}

@Composable
fun UploadAudioBox(
    audioPath: String? = null,
    duration: Long,
    currentPosition: Long,
    onPreviewClick: () -> Unit,
    onStopClick: () -> Unit,
    onSeek: (Long) -> Unit,
    onAudioSelected: (Uri) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .border(
                1.dp,
                Color(0xFFCBD5E1),
                RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        val audioLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let { onAudioSelected(it) }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            if (audioPath != null) {

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onPreviewClick) {
                        Text("Preview")
                    }
                    OutlinedButton(onClick = onStopClick) {
                        Text("Stop")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Slider(
                    value = currentPosition.toFloat(),
                    onValueChange = { onSeek(it.toLong()) },
                    valueRange = 0f..duration.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(formatDuration(currentPosition))
                    Text(formatDuration(duration))
                }

            } else {
                Icon(Icons.Default.MusicNote, null, tint = Color(0xFF9D50BB), modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("Upload MP3 file", fontWeight = FontWeight.Bold)
                Text("Drag and drop or tap to select files (Max 20MB)", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { audioLauncher.launch("audio/*") },
                shape = RoundedCornerShape(50)
            ) {
                Text(audioPath?.substringAfterLast("/") ?: "Upload Audio")
            }
        }
    }
}

@Composable
fun UploadImageBox(
    imagePath: String?,
    onImageSelected: (Uri) -> Unit,
) {
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onImageSelected(it) }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (imagePath != null) {
            AsyncImage(
                model = imagePath,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0))
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text("Upload Image", fontWeight = FontWeight.Bold)
            Text("Jpg, Png, Gif, ...", color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { imageLauncher.launch("image/*") },
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Upload Image", color = Color(0xFF9D50BB))
                }
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
                if (isSaving) "Saving....." else "Save Song",
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
            containerColor = Color(0xFFC5C5C5)
        )
    ) {
        Text(
            text = "Cancel",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}



