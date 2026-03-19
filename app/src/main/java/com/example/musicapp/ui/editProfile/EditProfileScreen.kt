package com.example.musicapp.ui.editProfile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.musicapp.AppViewModelFactory
import com.example.musicapp.R
import java.io.File

@Composable
fun EditProfileRoute(
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: EditProfileViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val imagePicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->

            uri ?: return@rememberLauncherForActivityResult

            try {

                val fileName = "avatar_${System.currentTimeMillis()}.jpg"
                val avatarDir = File(context.filesDir, "avatars")

                if(!avatarDir.exists()) {
                    avatarDir.mkdirs()
                }

                val file = File(avatarDir, fileName)

                context.contentResolver.openInputStream(uri)?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                viewModel.onAvatarChange(file.absolutePath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    LaunchedEffect(uiState.saveSuccess) {
        if(uiState.saveSuccess) {
            onSaveSuccess()
            viewModel.onSaveHandled()
        }
    }

    EditProfileScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onAvatarChange = { imagePicker.launch("image/*") },
        onNameChange = viewModel::onNameChange,
        onEmailChange = viewModel::onEmailChange,
        onBioChange = viewModel::onBioChange,
        onSave = viewModel::onSave
    )
}

@Composable
fun EditProfileScreen(
    uiState: EditProfileUiState,
    onBackClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onAvatarChange: () -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F4FA))
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        EditProfileTopBar(onBackClick)

        Spacer(Modifier.height(24.dp))

        AvatarCard(
            avatarUrl = uiState.avatarUrl,
            onAvatarChange = onAvatarChange
        )

        Spacer(Modifier.height(24.dp))

        ProfileTextField(
            label = "Name",
            value = uiState.userName,
            onValueChange = onNameChange
        )

        ProfileTextField(
            label = "Email",
            enabled = false,
            value = uiState.email,
            onValueChange = onEmailChange
        )

        ProfileTextField(
            label = "Bio",
            value = uiState.bio ?: "",
            onValueChange = onBioChange,
            minLines = 5
        )

        Spacer(Modifier.height(32.dp))

        uiState.errorMessage?.let {
            Spacer(Modifier.height(12.dp))
            Text(
                text = it,
                color = Color.Red,
                fontWeight = FontWeight.Medium
            )
        }

        SaveButton(
            isSaving = uiState.isSaving,
            enabled = true,
            onClick = onSave
        )

        Spacer(Modifier.height(18.dp))

        CancelButton(
            onClick = onBackClick
        )
    }
}

@Composable
fun EditProfileTopBar(
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null
            )
        }

        Text(
            text = "Edit Profile",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun AvatarCard(
    avatarUrl: String?,
    onAvatarChange: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = avatarUrl ?: R.drawable.ic_account,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_account),
                error = painterResource(R.drawable.ic_account)
            )

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.BottomEnd)
                    .background(Color(0xFF7A3EF0), CircleShape)
                    .clickable { onAvatarChange() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit,
    minLines: Int = 1
) {
    Text(
        text = label,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )

    OutlinedTextField(
        value = value,
        enabled = enabled,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        minLines = minLines
    )
}

@Composable
fun SaveButton(
    isSaving: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isSaving,
        shape = RoundedCornerShape(32.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF7A3EF0)
        )
    ) {
        Text(
            text = if (isSaving) "Saving..." else "Save Profile",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun CancelButton(
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(32.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White
        )
    ) {
        Text(
            text = "Cancel",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF7A3EF0)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfilePreview() {
    EditProfileScreen(
        uiState = EditProfileUiState(
            userName = "Nguyen Van A",
            email = "john.mclean@examplepetstore.com",
            bio = "This is a bio",
            avatarUrl = null
        ),
        onBackClick = {},
        onNameChange = {},
        onEmailChange = {},
        onBioChange = {},
        onAvatarChange = {},
        onSave = {}
    )
}