package com.example.musicapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() },
    ) {
        AlbumImage(imageUrl = album.imageUrl)

        Spacer(modifier = Modifier.height(10.dp))

        AlbumInformation(
            albumName = album.title,
            releaseDate = album.releaseDate
        )
    }
}

@Composable
fun AlbumImage(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        modifier = modifier
            .size(176.dp)
            .shadow(10.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp)),
        contentScale = ContentScale.Crop,
        placeholder = painterResource(R.drawable.ic_error),
        error = painterResource(R.drawable.ic_error)
    )
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
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF1A1A1A),
            maxLines = 1
        )

        if (releaseDate != null) {
            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = releaseDate,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF9E9E9E),
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