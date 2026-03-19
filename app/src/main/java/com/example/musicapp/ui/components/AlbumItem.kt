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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.musicapp.R
import com.example.musicapp.domain.model.Album

@Composable
fun AlbumItem(
    album: Album,
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
            AlbumImage(imageUrl = album.imageUrl)
            Spacer(modifier = Modifier.height(8.dp))
            AlbumInformation(
                albumName = album.title,
                releaseDate = album.releaseDate
            )
        }
    }
}

@Composable
fun AlbumImage(
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
fun AlbumInformation(
    albumName: String,
    releaseDate: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = albumName,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1
        )

        if (releaseDate != null) {
            Text(
                text = releaseDate,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 1
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlbumItemPreview() {
    AlbumItem(
        album = Album(
            id = 1L,
            title = "Album Title",
            artistId = 1L,
            releaseDate = "2023",
            imageUrl = null
        ),
        onClick = {}
    )
}