package com.example.musicapp.ui.library.artist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicapp.AppViewModelFactory
import com.example.musicapp.domain.model.Artist

@Composable
fun ArtistsRoute(
    onArtistClick: (Long) -> Unit,
    viewModel: ArtistsViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {

    val uiState by viewModel.uiState.collectAsState()

    ArtistsScreen(
        uiState = uiState,
        onArtistClick = onArtistClick,
        onSearch = viewModel::onSearch,
        onSortChange = viewModel::setSort,
        onFollowClick = viewModel::toggleFollowArtist,
        onNext = viewModel::nextPage,
        onPrev = viewModel::prevPage,
        onPageClick = viewModel::goToPage
    )
}

@Composable
fun ArtistsScreen(
    uiState: ArtistsUiState,
    onArtistClick: (Long) -> Unit,
    onSearch: (String) -> Unit,
    onSortChange: (ArtistSortType) -> Unit,
    onFollowClick: (Long) -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onPageClick: (Int) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6FA))
    ) {

        ArtistSearchBar(
            query = uiState.query,
            onSearch = onSearch
        )

        ArtistFilterRow(
            selectedSort = uiState.sortType,
            onSortChange = onSortChange
        )

        Spacer(Modifier.height(8.dp))

        LazyColumn {
            if (uiState.artists.isEmpty()) {
                item {
                    Text(
                        text = "No artists available",
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(uiState.artists) { artist ->

                    val isFollowed = uiState.followedArtistIds.contains(artist.id)

                    ArtistItem(
                        artist = artist,
                        isFollowed = isFollowed,
                        onClick = { onArtistClick(artist.id) },
                        onFollowClick = { onFollowClick(artist.id) }
                    )
                }
            }

            item {
                Pagination(
                    currentPage = uiState.currentPage,
                    totalPages = uiState.totalPages,
                    onNext = onNext,
                    onPrev = onPrev,
                    onPageClick = onPageClick
                )
            }
        }
    }
}

@Composable
fun ArtistSearchBar(
    query: String,
    onSearch: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onSearch,
        placeholder = { Text("Search artists by name...") },
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
}

@Composable
fun ArtistFilterRow(
    selectedSort: ArtistSortType,
    onSortChange: (ArtistSortType) -> Unit
) {

    val activeColor = Color(0xFFFF2EC7)

    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        FilterButton(
            text = "A-Z",
            selected = selectedSort == ArtistSortType.AZ,
            activeColor = activeColor
        ) {
            onSortChange(ArtistSortType.AZ)
        }

        FilterButton(
            text = "Favorites",
            selected = selectedSort == ArtistSortType.FAVORITES,
            activeColor = activeColor
        ) {
            onSortChange(ArtistSortType.FAVORITES)
        }

        FilterButton(
            text = "Popularity",
            selected = selectedSort == ArtistSortType.POPULARITY,
            activeColor = activeColor
        ) {
            onSortChange(ArtistSortType.POPULARITY)
        }

    }
}

@Composable
fun FilterButton(
    text: String,
    selected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {

    val background = if (selected) activeColor else Color.Transparent
    val textColor = if (selected) Color.White else Color.Black
    val borderColor = activeColor

    OutlinedButton(
        onClick = onClick,
        border = BorderStroke(1.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = background
        ),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = text,
            color = textColor
        )
    }
}

@Composable
fun ArtistItem(
    artist: Artist,
    isFollowed: Boolean = false,
    onClick: () -> Unit,
    onFollowClick: (Long) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        AsyncImage(
            model = artist.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .border(
                    width = 1.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(24.dp)
                ),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = artist.name,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "${artist.popularity} popularity",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        IconButton(
            onClick = { onFollowClick(artist.id) },
            modifier = Modifier
                .size(48.dp)
                .background(
                    Color.Black.copy(alpha = 0.4f),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = if (isFollowed)
                    Icons.Default.Favorite
                else
                    Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = if (isFollowed) Color(0xFFFF2EC7) else Color.White
            )
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