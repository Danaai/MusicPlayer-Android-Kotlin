package com.example.musicapp.ui.admin.artist_management

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicapp.AppViewModelFactory
import java.io.File

@Composable
fun AddArtistRoute(
    artistId: Long?,
    onBackClick: () -> Unit,
    viewModel: AddArtistViewModel = viewModel(factory = AppViewModelFactory(
        context = LocalContext.current,
        artistId = artistId)
    )
) {

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.saveSuccess) {
        if(uiState.saveSuccess) {
            viewModel.consumeSaveSuccess()
            onBackClick()
        }
    }

    AddArtistScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onNameChange = viewModel::onNameChange,
        onImageSelected = viewModel::onImageSelected,
        onInfoChange = viewModel::onInfoChange,
        onPopularityChange = viewModel::onPopularityChange,
        onSaveClick = viewModel::saveArtist
    )

}

@Composable
fun AddArtistScreen(
    uiState: AddArtistUiState,
    onBackClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onInfoChange: (String) -> Unit,
    onPopularityChange: (Float) -> Unit,
    onImageSelected: (String) -> Unit,
    onSaveClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FB))
    ) {

        AddArtistTopBar(onBackClick, uiState.name)

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

                            Text("Artist's name", fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = uiState.name,
                                onValueChange = onNameChange,
                                placeholder = {
                                    Text("Enter the artist's full name.")
                                },
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Column {

                            Text("Artist's photo", fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(8.dp))

                            UploadArtistBox(
                                imageUrl = uiState.imageUrl,
                                onImageSelected = onImageSelected
                            )
                        }

                        Column {
                            Text("Artist's information", fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = uiState.info,
                                onValueChange = onInfoChange,
                                placeholder = {
                                    Text("Enter a biography, career, or detailed description of this artist...")
                                },
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                            )
                        }

                        // ===== POPULARITY =====

                        PopularitySlider(
                            popularity = uiState.popularity,
                            onPopularityChange = onPopularityChange
                        )

                        // ===== BUTTONS =====
                        SaveButton(
                            isSaving = uiState.isSaving,
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
fun AddArtistTopBar(
    onBackClick: () -> Unit,
    nameArtist: String
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
            if(nameArtist.isBlank()) "Add new artist" else "Edit artist $nameArtist",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF0F172A)
        )
    }
}

@Composable
fun UploadArtistBox(
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
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp))
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
fun PopularitySlider(
    popularity: Float,
    onPopularityChange: (Float) -> Unit
) {
    Column {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Popularity", fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF9D50BB).copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    "${popularity.toInt()}%",
                    color = Color(0xFF9D50BB),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Slider(
            value = popularity,
            onValueChange = onPopularityChange,
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF9D50BB),
                activeTrackColor = Color(0xFF9D50BB)
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("EMERGING", color = Color(0xFF64748B), fontSize = 12.sp)
            Spacer(modifier = Modifier.weight(1f))
            Text("SUPERSTAR", color = Color(0xFF64748B), fontSize = 12.sp)
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
                if (isSaving) "Saving....." else "Save Artist",
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