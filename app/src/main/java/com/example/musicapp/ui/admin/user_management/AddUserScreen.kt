package com.example.musicapp.ui.admin.user_management

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicapp.AppViewModelFactory

@Composable
fun AddUserRoute(
    userId: Long? = null,
    onBackClick: () -> Unit,
    viewModel: AddUserViewModel = viewModel(factory = AppViewModelFactory(
        LocalContext.current,
        userId = userId)
    )
) {
    
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.saveSuccess) {
        if(uiState.saveSuccess) {
            viewModel.consumeSaveSuccess()
            onBackClick()
        }
    }

    AddUserScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onUsernameChange = viewModel::onUsernameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onRoleChange = viewModel::onRoleChange,
        onSaveClick = viewModel::saveUser,
    )
    
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserScreen(
    uiState: AddUserUiState,
    onBackClick: () -> Unit,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRoleChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    var role by remember { mutableStateOf("user") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FB))
    ) {
        AddUserTopBar(
            onBackClick = onBackClick,
            userName = uiState.userName
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                AvatarSection(
                    avatarUrl = uiState.avtUrl
                )
            }

            item {
                RoundedTextField(
                    label = "Full name",
                    value = uiState.userName,
                    placeholder = "Ex: John Doe",
                    icon = Icons.Default.AccountCircle,
                    onValueChange = onUsernameChange
                )
            }

            item {
                RoundedTextField(
                    label = "Email",
                    value = uiState.email,
                    placeholder = "Ex: johndoe@gmail.com",
                    icon = Icons.Default.Email,
                    onValueChange = onEmailChange
                )
            }

            item {
                RoundedTextField(
                    label = "Password",
                    value = uiState.password,
                    placeholder = "Ex: ********",
                    icon = Icons.Default.Password,
                    onValueChange = onPasswordChange
                )
            }

            item {
                BioTextField(
                    label = "Bio",
                    value = uiState.bio ?: "",
                    placeholder = "Ex: I am a software engineer",
                    icon = Icons.Default.Description
                )
            }

            item {
                RoleDropdown(
                    selectedRole = uiState.role,
                    onRoleSelected = onRoleChange
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))

                SaveButton(
                    text = if (uiState.isSaving) "Saving......" else "Save",
                    onClick = onSaveClick
                )

                Spacer(modifier = Modifier.height(12.dp))

                CancelButton(onBackClick)
            }
        }
    }
}

@Composable
fun AddUserTopBar(
    onBackClick: () -> Unit,
    userName: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onBackClick) {
            Icon(Icons.Default.ArrowBack, contentDescription = null)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            if (userName.isBlank()) "Add new User" else "Edit user $userName",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF0F172A)
        )
    }
}

@Composable
fun AvatarSection(
    avatarUrl: String?,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {

            if (avatarUrl.isNullOrEmpty()) {

                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Color.White
                )

            } else {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Text(
            "Avatar Profile",
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RoundedTextField(
    label: String,
    value: String,
    placeholder: String,
    icon: ImageVector,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        Text(label, fontWeight = FontWeight.Medium)

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(icon, null)
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedBorderColor = Color(0xFF6366F1),
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )
    }
}

@Composable
fun BioTextField(
    label: String,
    value: String,
    icon: ImageVector,
    placeholder: String,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier) {

        Text(label, fontWeight = FontWeight.Medium)

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = {},
            enabled = false,
            placeholder = { Text(placeholder) },
            shape = RoundedCornerShape(20),
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            leadingIcon = {
                Icon(icon, null)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleDropdown(
    selectedRole: String,
    onRoleSelected: (String) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    val roles = listOf("user", "admin")

    Column(modifier = Modifier.fillMaxWidth()) {

        Text("Role", fontWeight = FontWeight.Medium)

        Spacer(Modifier.height(6.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {

            OutlinedTextField(
                value = selectedRole,
                onValueChange = {},
                readOnly = true,
                leadingIcon = {
                    Icon(Icons.Default.Person, null)
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(30)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {

                roles.forEach { role ->

                    DropdownMenuItem(
                        text = { Text(role) },
                        onClick = {
                            onRoleSelected(role)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SaveButton(
    text: String,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .clip(RoundedCornerShape(50))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF9D50BB), Color(0xFFE81CFF))
                )
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CancelButton(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFC5C5C5)
        )
    ) {
        Text(
            text = "Cancel",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}
