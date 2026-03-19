package com.example.musicapp.ui.admin

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicapp.AppViewModelFactory
import com.example.musicapp.ui.settings.ConfirmLogoutDialog
import java.text.NumberFormat

@Composable
fun AdminDashBoardRoute(
    onManageSongsClick: () -> Unit,
    onManageArtistsClick: () -> Unit,
    onManageAlbumClick: () -> Unit,
    onManageUserClick: () -> Unit,
    viewModel: AdminViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {

    val uiState by viewModel.uiState.collectAsState()

    var showLogoutConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.refreshAdmin()
    }

    if(showLogoutConfirm) {
        ConfirmLogoutDialog(
            onConfirm = {
                showLogoutConfirm = false
                viewModel.logout()
            },
            onDismiss = {
                showLogoutConfirm = false
            }
        )
    }

    AdminDashboardScreen(
        uiState = uiState,
        onLogoutClick = {
            showLogoutConfirm = true
        },
        onManageSongsClick = onManageSongsClick,
        onManageArtistsClick = onManageArtistsClick,
        onManageAlbumClick = onManageAlbumClick,
        onManageUserClick = onManageUserClick
    )
}

@Composable
fun AdminDashboardScreen(
    uiState: AdminUiState,
    onLogoutClick: () -> Unit,
    onManageSongsClick: () -> Unit,
    onManageArtistsClick: () -> Unit,
    onManageAlbumClick: () -> Unit,
    onManageUserClick: () -> Unit
) {

    val formattedSongs = NumberFormat.getInstance().format(uiState.totalSongs)
    val formattedUsers = NumberFormat.getInstance().format(uiState.totalUsers)
    val formattedArtists = NumberFormat.getInstance().format(uiState.totalArtists)
    val formattedAlbums = NumberFormat.getInstance().format(uiState.totalAlbums)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F6FA)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item { AdminTopBar(onLogoutClick) }

            item { GreetingSection() }

            item {
                StatsGrid(
                    totalSongs = formattedSongs,
                    totalUsers = formattedUsers,
                    totalArtists = formattedArtists,
                    totalAlbums = formattedAlbums,
                )
            }

            item { StreamingSection(Color(0xFFFFFFFF), Color(0xFFE5E7EB), Color(0xFF7C3AED)) }

            item {
                QuickActionsSection(
                    Color(0xFF7C3AED),
                    Color(0xFFE5E7EB),
                    onManageSongsClick,
                    onManageArtistsClick,
                    onManageAlbumClick,
                    onManageUserClick
                )
            }

            item { RecentUploadsSection(Color(0xFFFFFFFF), Color(0xFFE5E7EB), Color(0xFF7C3AED)) }

            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}

@Composable
private fun AdminTopBar(
    onLogoutClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile",
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
        )

        Text(
            text = "Admin Dashboard",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.Black
        )

        IconButton(
            onClick = onLogoutClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Red.copy(alpha = 0.5f)),
        ) {
            Icon(
                Icons.Default.Logout,
                null,
                tint = Color.White
            )
        }
    }
}

@Composable
private fun GreetingSection() {
    Column {
        Text(
            "Good morning, Admin",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Here's what's happening with your music library today.",
            color = Color.Gray
        )
    }
}

@Composable
private fun StatsGrid(
    totalSongs: String,
    totalUsers: String,
    totalArtists: String,
    totalAlbums: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            StatCard(
                title = "Total Users",
                value = totalUsers,
                percent = "0%",
                icon = Icons.Default.AccountBox,
                cardColor = Color(0xFFFFFFFF),
                borderColor = Color(0xFFE5E7EB),
                primary = Color(0xFF7C3AED),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            )

            StatCard(
                title = "Total Songs",
                value = totalSongs,
                percent = "0%",
                icon = Icons.Default.MusicNote,
                cardColor = Color(0xFFFFFFFF),
                borderColor = Color(0xFFE5E7EB),
                primary = Color(0xFF7C3AED),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            StatCard(
                title = "Total Artists",
                value = totalArtists,
                percent = "0%",
                icon = Icons.Default.SupervisorAccount,
                cardColor = Color(0xFFFFFFFF),
                borderColor = Color(0xFFE5E7EB),
                primary = Color(0xFF7C3AED),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            )

            StatCard(
                title = "Total Albums",
                value = totalAlbums,
                percent = "0%",
                icon = Icons.Default.PhotoAlbum,
                cardColor = Color(0xFFFFFFFF),
                borderColor = Color(0xFFE5E7EB),
                primary = Color(0xFF7C3AED),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    percent: String,
    icon: ImageVector,
    cardColor: Color,
    borderColor: Color,
    primary: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(cardColor)
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = primary)
                }

                Text(percent, color = Color(0xFF10B981))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(title, color = Color.Gray)

            Spacer(Modifier.height(8.dp))

            Text(
                value,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun StreamingSection(
    cardColor: Color,
    borderColor: Color,
    primary: Color
) {
    var selected by remember { mutableStateOf("Month") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(cardColor)
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Column {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Streaming Activity", fontWeight = FontWeight.Bold)

                Row {
                    FilterChip("Week", selected == "Week") { selected = "Week" }
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip("Month", selected == "Month", primary) {
                        selected = "Month"
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                listOf(40, 70, 60, 100, 75, 80, 65).forEach {
                    Box(
                        modifier = Modifier
                            .width(20.dp)
                            .height(it.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(primary, primary.copy(alpha = 0.5f))
                                )
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    selected: Boolean,
    primary: Color = Color(0xFF7C3AED),
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) primary else Color(0xFFE5E7EB),
            contentColor = if (selected) Color.White else Color.Black
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 12.sp)
    }
}

@Composable
private fun QuickActionsSection(
    primary: Color,
    borderColor: Color,
    onManageSongsClick: () -> Unit,
    onManageArtistsClick: () -> Unit,
    onManageAlbumClick: () -> Unit,
    onManageUserClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Text(
            "Quick Actions",
            fontWeight = FontWeight.Bold
        )

        GradientActionButton(
            text = "Manage Songs",
            icon = Icons.Default.MusicNote,
            primary = primary,
            onClick = onManageSongsClick
        )

        GradientActionButton(
            text = "Manage Artists",
            icon = Icons.Default.SupervisorAccount,
            primary = primary,
            onClick = onManageArtistsClick
        )

        GradientActionButton(
            text = "Manage Albums",
            icon = Icons.Default.PhotoAlbum,
            primary = primary,
            onClick = onManageAlbumClick
        )

        GradientActionButton(
            text = "Manage User",
            icon = Icons.Default.AccountBox,
            primary = primary,
            onClick = onManageUserClick
        )
    }
}

@Composable
fun GradientActionButton(
    text: String,
    icon: ImageVector,
    primary: Color,
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }

    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsPressedAsState()

    val isActive = isPressed || isHovered

    val backgroundBrush = if (isActive) {
        Brush.horizontalGradient(
            listOf(primary, Color(0xFF9333EA))
        )
    } else {
        Brush.horizontalGradient(
            listOf(Color.White, Color.White)
        )
    }

    val contentColor by animateColorAsState(
        if (isActive) Color.White else primary,
        label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(backgroundBrush)
            .border(1.5.dp, primary, RoundedCornerShape(50))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            }
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Icon(icon, contentDescription = null, tint = contentColor)

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )

            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = contentColor
            )
        }
    }
}

@Composable
private fun RecentUploadsSection(
    cardColor: Color,
    borderColor: Color,
    primary: Color
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(cardColor)
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Recent Uploads", fontWeight = FontWeight.Bold)
                Text("View all", color = primary)
            }
        }
    }
}

