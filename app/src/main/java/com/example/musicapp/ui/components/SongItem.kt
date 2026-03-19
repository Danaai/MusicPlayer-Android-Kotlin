package com.example.musicapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.musicapp.R
import com.example.musicapp.domain.model.Song

enum class SongItemType {
    HOME,
    MINI_PLAYER,
}

@Composable
fun SongItem(
    song: Song,
    artistName: String,
    songType: SongItemType = SongItemType.HOME,
    index: Int = 0,
    isPlaying: Boolean = false,
    progress: Float = 0f,
    onClick: () -> Unit,
    onMoreClick: (() -> Unit)? = null,
    onPlayPauseClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onPreviousClick: () -> Unit = {},
    onSeek: (Float) -> Unit = {},
    modifier: Modifier = Modifier
) {
    when (songType) {

        SongItemType.HOME ->
            HomeSongItem(
                index = index,
                song = song,
                isPlaying = isPlaying,
                onClick = onClick,
                onMoreClick = { onMoreClick?.invoke() },
                modifier = modifier,
                artistName = artistName
            )

        SongItemType.MINI_PLAYER ->
            MiniPlayerSongItem(
                title = song.title,
                artistName = artistName,
                imageUrl = song.imageUrl,
                isPlaying = isPlaying,
                progress = progress,
                onClick = onClick,
                onPlayPauseClick = onPlayPauseClick,
                onNextClick = onNextClick,
                onPreviousClick = onPreviousClick,
                onSeek = onSeek
            )

    }
}

@Composable
fun HomeSongItem(
    index: Int,
    song: Song,
    artistName: String,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val highlightColor =
        if(isPlaying) Color.Magenta.copy(alpha = 0.08f)
        else Color.Transparent

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(highlightColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = index.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isPlaying) Color.Magenta else Color.Gray,
            modifier = Modifier.width(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp)
        )
        SongImage(
            imageUrl = song.imageUrl,
            songType = SongItemType.HOME
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column( modifier = Modifier.weight(1f) ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Medium
            )
            Text(
                text = artistName,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 1
            )
        }
        Text(
            text = formatDuration(song.durationMs),
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(end = 4.dp)
        )
        IconButton(onClick = onMoreClick) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun MiniPlayerSongItem (
    title: String,
    artistName: String,
    imageUrl: String?,
    isPlaying: Boolean = false,
    progress: Float = 0f,
    onClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onSeek: (Float) -> Unit
) {

    Surface(
        tonalElevation = 6.dp,
        shadowElevation = 10.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(Color.Gray.copy(alpha = 0.2f))
                    .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val newProgress = offset.x / size.width
                        onSeek(newProgress.coerceIn(0f, 1f))
                    }
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(Color.Magenta)
                )
            }

            Row(
                modifier = Modifier
                    .clickable { onClick() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                SongImage(
                    imageUrl = imageUrl,
                    songType = SongItemType.MINI_PLAYER
                )

                Spacer(modifier = Modifier.width(12.dp))

                SongInformation(
                    title = title,
                    artistName = artistName,
                    songType = SongItemType.MINI_PLAYER,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(onClick = onPreviousClick) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = null
                        )
                    }

                    IconButton(onClick = onPlayPauseClick) {
                        Icon(
                            imageVector = if (isPlaying)
                                Icons.Default.Pause
                            else
                                Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                    }

                    IconButton(onClick = onNextClick) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}


@Composable
fun SongImage(
    imageUrl: String?,
    songType: SongItemType
) {

    val modifier = if(songType == SongItemType.MINI_PLAYER)
        Modifier.size(48.dp)
    else
        Modifier.size(56.dp)

    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        modifier = modifier
            .clip(CircleShape)
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(24.dp)
            ),
        contentScale = ContentScale.Crop,
        placeholder = painterResource(R.drawable.ic_error),
        error = painterResource(R.drawable.ic_error)
    )
}

@Composable
fun SongInformation(
    title: String,
    artistName: String,
    songType: SongItemType,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            style = if (songType == SongItemType.MINI_PLAYER)
                MaterialTheme.typography.titleMedium
            else
                MaterialTheme.typography.titleLarge,
            maxLines = 1
        )

        Text(
            text = artistName,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SongItemPreview() {
    SongItem(
        song = Song(
            id = 1L,
            title = "Song Title",
            artistId = 1L,
            durationMs = 180000L,
            imageUrl = null,
            albumId = 1L,
            audioUrl = ""
        ),
        songType = SongItemType.MINI_PLAYER,
        artistName = "Artist Name",
        onClick = {}
    )
}
