package com.example.musicapp.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicapp.AppViewModelFactory
import com.example.musicapp.R
import java.io.File

@Composable
fun SettingsRoute(
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onHelpClick: () -> Unit,
    onAboutClick: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLogoutConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.refreshUser()
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

    SettingsScreen(
        uiState = uiState,
        onProfileClick = onProfileClick,
        onMoreClick = {},
        onNotificationClick = onNotificationClick,
        onHelpClick = onHelpClick,
        onAboutClick = onAboutClick,
        onDarkModeChanged = viewModel::onDarkModeChange,
        onEditProfileClick = onEditProfileClick,
        onFavoritesClick = onFavoritesClick,
        onHistoryClick = onHistoryClick,
        onLogout = {
            showLogoutConfirm = true
        }
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onProfileClick: () -> Unit,
    onMoreClick: () -> Unit,
    onDarkModeChanged: (Boolean) -> Unit,
    onNotificationClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onHelpClick: () -> Unit,
    onAboutClick: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        SettingsTopBar(
            avatarUrl = uiState.avatarUrl,
            onProfileClick = onProfileClick,
            onNotificationClick = onNotificationClick,
            onMoreClick = onMoreClick
        )

        Text(
            text = "Customize",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(start = 16.dp, top = 18.dp)
        )

        SettingsGroup {
            SettingItem(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                onClick = onNotificationClick
            )
            Divider(
                Modifier
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFFF2EEF6))
            )
            SettingItem(
                icon = Icons.Default.AccountCircle,
                title = "Edit Profile",
                onClick = onEditProfileClick
            )
            Divider(
                Modifier
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFFF2EEF6))
            )
            SettingItem(
                icon = Icons.Default.Favorite,
                title = "Favorites",
                onClick = onFavoritesClick
            )
            Divider(
                Modifier
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFFF2EEF6))
            )
            SettingItem(
                icon = Icons.Default.History,
                title = "History",
                onClick = onHistoryClick
            )
            Divider(
                Modifier
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFFF2EEF6))
            )
            SettingSwitchItem(
                icon = Icons.Default.DarkMode,
                title = "Dark Mode",
                checked = uiState.isDarkMode,
                onCheckedChange = onDarkModeChanged
            )
        }

        Text(
            text = "Support",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(start = 16.dp)
        )

        SettingsGroup {
            SettingItem(
                icon = Icons.Default.Help,
                title = "Help Center",
                onClick = onHelpClick
            )
            Divider(
                Modifier
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFFF2EEF6))
            )
            SettingItem(
                icon = Icons.Default.Info,
                title = "About Us",
                subtitle = stringResource(R.string.version_app),
                showArrow = false,
                onClick = onAboutClick
            )
        }

        if (uiState.isLoggedIn) {
            LogoutButton(onLogout = onLogout)
        }
    }
}

@Composable
fun SettingsTopBar(
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
            text = "Settings",
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

@Composable
fun SettingsGroup(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    showArrow: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF7A3EF0)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = title,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        if(showArrow) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null
            )
        } else {
            Text(
                text = subtitle ?: "",
                fontSize = 13.sp,
                color = Color.Gray,
            )
        }
    }
}

@Composable
fun SettingSwitchItem(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF7A3EF0)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = title,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun LogoutButton(
    onLogout: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFADDDD)
        ),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .fillMaxWidth()
            .clickable { onLogout() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Logout",
                color = Color(0xFFD32F2F),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
        }
    }
}

@Composable
fun ConfirmLogoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Logout",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text("Are you sure you want to logout?")
        },
        confirmButton = {
            Text(
                text = "Logout",
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onConfirm() }
            )
        },
        dismissButton = {
            Text(
                text = "Cancel",
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onDismiss() }
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    SettingsScreen(
        uiState = SettingsUiState(
            isLoggedIn = true,
            userName = "Nguyen Van A",
            email = "james.k.polk@examplepetstore.com",
            avatarUrl = "",
            isDarkMode = false
        ),
        onProfileClick = {},
        onMoreClick = {},
        onNotificationClick = {},
        onEditProfileClick = {},
        onFavoritesClick = {},
        onHistoryClick = {},
        onHelpClick = {},
        onAboutClick = {},
        onDarkModeChanged = {},
        onLogout = {}
    )
}