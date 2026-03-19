package com.example.musicapp.ui.library.album

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicapp.AppViewModelFactory
import com.example.musicapp.R
import com.example.musicapp.domain.model.Album
import com.example.musicapp.ui.library.artist.FilterButton

@Composable
fun AlbumsRoute(
    onAlbumClick: (Long) -> Unit,
    viewModel: AlbumsViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {

    val uiState by viewModel.uiState.collectAsState()

    AlbumsScreen(
        uiState = uiState,
        onAlbumClick = onAlbumClick,
        onSearch = viewModel::onSearch,
        onSortChange = viewModel::setSort,
        onNext = viewModel::nextPage,
        onPrev = viewModel::prevPage,
        onPageClick = viewModel::goToPage
    )

}

@Composable
fun AlbumsScreen(
    uiState: AlbumsUiState,
    onAlbumClick: (Long) -> Unit,
    onSearch: (String) -> Unit,
    onSortChange: (AlbumSortType) -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onPageClick: (Int) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6FA))
    ) {

        AlbumSearchBar(
            query = uiState.query,
            onSearch = onSearch
        )

        AlbumFilterRow(
            selectedSort = uiState.sortType,
            onSortChange = onSortChange
        )

        Spacer(Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            items(uiState.albums) { album ->
                AlbumItem(
                    album = album,
                    onAlbumClick = { onAlbumClick(album.id) }
                )
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
fun AlbumSearchBar(
    query: String,
    onSearch: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onSearch,
        placeholder = { Text("Search albums by title...") },
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
fun AlbumFilterRow(
    selectedSort: AlbumSortType,
    onSortChange: (AlbumSortType) -> Unit
) {

    val activeColor = Color(0xFFFF2EC7)

    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        FilterButton(
            text = "A-Z",
            selected = selectedSort == AlbumSortType.AZ,
            activeColor = activeColor
        ) {
            onSortChange(AlbumSortType.AZ)
        }

        FilterButton(
            text = "Latest",
            selected = selectedSort == AlbumSortType.LATEST,
            activeColor = activeColor
        ) {
            onSortChange(AlbumSortType.LATEST)
        }
    }
}

@Composable
fun AlbumItem(
    album: Album,
    onAlbumClick: () -> Unit
) {

    Column(
        modifier = Modifier.clickable { onAlbumClick() }
    ) {

        AsyncImage(
            model = album.imageUrl,
            contentDescription = album.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 1.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(24.dp)
                ),
            error = painterResource(R.drawable.ic_error),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = album.title,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = album.artistId.toString(),
            style = MaterialTheme.typography.bodySmall
        )
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



