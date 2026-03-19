package com.example.musicapp.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.musicapp.AppViewModelFactory
import com.example.musicapp.R
import com.example.musicapp.navigation.AppDestination
import com.example.musicapp.ui.library.album.AlbumsRoute
import com.example.musicapp.ui.library.artist.ArtistsRoute
import com.example.musicapp.ui.library.playlist.PlaylistManagementRoute
import com.example.musicapp.ui.library.song.SongsRoute
import com.example.musicapp.ui.player.PlayerViewModel
import kotlinx.coroutines.launch

@Composable
fun LibraryRoute(
    onProfileClick: () -> Unit,
    navController: NavHostController,
    playerViewModel: PlayerViewModel,
    initialTab: LibraryTab = LibraryTab.PLAYLISTS,
    viewModel: LibraryViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.selectTab(initialTab)
    }

    LibraryScreen(
        uiState = uiState,
        navController = navController,
        playerViewModel = playerViewModel,
        selectedTab = uiState.selectedTab,
        onTabSelected = viewModel::selectTab,
        onProfileClick = onProfileClick,
        onNotificationClick = {},
        onMoreClick = {}
    )
}

@Composable
fun LibraryScreen(
    uiState: LibraryUiState,
    navController: NavHostController,
    playerViewModel: PlayerViewModel,
    selectedTab: LibraryTab,
    onTabSelected: (LibraryTab) -> Unit,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onMoreClick: () -> Unit,
) {

    val tabs = listOf(
        "Playlists",
        "Artists",
        "Albums",
        "Songs"
    )

    val pagerState = rememberPagerState(
        initialPage = selectedTab.ordinal,
        pageCount = { tabs.size }
    )

    LaunchedEffect(selectedTab) {
        pagerState.animateScrollToPage(selectedTab.ordinal)
    }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        LibraryTopBar(
            avatarUrl = uiState.avatarUrl,
            onProfileClick = onProfileClick,
            onNotificationClick = onNotificationClick,
            onMoreClick = onMoreClick
        )

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.White,
            contentColor = Color.Magenta
        ) {

            tabs.forEachIndexed { index, title ->

                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {

                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }

                        onTabSelected(
                            LibraryTab.values()[index]
                        )
                    },
                    text = { Text(title) }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->

            when (page) {
                0 -> PlaylistManagementRoute(
                    onPlaylistClick = {
                        navController.navigate(
                            AppDestination.PlaylistDetail.createRoute(it)
                        )
                    },
                    onAddPlaylist = {
                        navController.navigate(
                            AppDestination.AddPlaylist.route
                        )
                    },
                    onEditPlaylist = {
                        navController.navigate(
                            AppDestination.EditPlaylist.createRoute(it)
                        )
                    }
                )
                1 -> ArtistsRoute(
                    onArtistClick = {
                        navController.navigate(
                            AppDestination.ArtistDetail.createRoute(it)
                        )
                    }
                )
                2 -> AlbumsRoute(
                    onAlbumClick = {
                        navController.navigate(
                            AppDestination.AlbumDetail.createRoute(it)
                        )
                    }
                )
                3 -> SongsRoute(
                    playerViewModel = playerViewModel
                )
            }
        }
    }
}

@Composable
fun LibraryTopBar(
    avatarUrl: String? = null,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onMoreClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .shadow(1.dp)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        AsyncImage(
            model = avatarUrl ?: R.drawable.ic_account,
            contentDescription = "Profile",
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .clickable { onProfileClick() },
            contentScale = ContentScale.Crop
        )

        Text(
            text = "Library",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.Black
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
            ) {
                Icon(Icons.Default.Notifications, null)
            }

            Spacer(Modifier.width(8.dp))

            IconButton(
                onClick = onMoreClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
            ) {
                Icon(Icons.Default.MoreVert, null)
            }
        }
    }
}