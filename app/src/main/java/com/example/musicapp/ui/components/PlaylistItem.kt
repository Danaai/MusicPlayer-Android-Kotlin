package com.example.musicapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.musicapp.R
import com.example.musicapp.domain.model.Playlist

@Composable
fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
        ) {
            PlaylistImage(imageUrl = playlist.imageUrl)
            Spacer(modifier = Modifier.height(8.dp))
            PlaylistInformation(
                playlistName = playlist.name,
                style = playlist.description
            )
        }
    }
}

@Composable
fun PlaylistImage(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    Box() {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = modifier
                .size(176.dp)
                .clip(MaterialTheme.shapes.medium),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.ic_error),
            error = painterResource(R.drawable.ic_error)
        )
    }
}

@Composable
fun PlaylistInformation(
    playlistName: String,
    style: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = playlistName,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1
        )

        if (style != null) {
            Text(
                text = style,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 1
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlaylistItemPreview() {
    PlaylistItem(
        playlist = Playlist(
            id = 1L,
            name = "Chill Hits",
            description = "Relax & chill",
            ownerId = 1L,
            ownerName = "Spotify",
            imageUrl = null
        ),
        onClick = {}
    )
}