package com.example.musicapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.musicapp.R
import com.example.musicapp.domain.model.Artist
import com.example.musicapp.ui.theme.MusicAppTheme

@Composable
fun ArtistItem(
    artist: Artist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ArtistImage(imageUrl = artist.imageUrl)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = artist.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ArtistImage(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        modifier = Modifier
            .size(96.dp)
            .clip(MaterialTheme.shapes.medium)
        ,
        contentScale = ContentScale.Crop,
        placeholder = painterResource(R.drawable.ic_error),
        error = painterResource(R.drawable.ic_error)
    )
}

@Preview(showBackground = true)
@Composable
fun ArtistItemPreview() {
    MusicAppTheme {
        ArtistItem(
            artist = Artist(
                id = 1L,
                name = "Adele",
                imageUrl = null
            ),
            onClick = {}
        )
    }
}