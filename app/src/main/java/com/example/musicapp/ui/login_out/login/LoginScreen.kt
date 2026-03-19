package com.example.musicapp.ui.login_out.login

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicapp.AppViewModelFactory
import com.example.musicapp.R

@Composable
fun LoginRoute(
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    viewModel: LoginViewModel = viewModel(
        factory = AppViewModelFactory(LocalContext.current)
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    LoginScreen(
        uiState = uiState,
        onUsernameChange = viewModel::onUsernameChange,
        onPassChange = viewModel::onPassChange,
        onLogin = { viewModel.onLoginClick(onLoginSuccess) },
        onSignUpClick = onSignUpClick,
        onGoogleClick = {},
        onFacebookClick = {},
        onTelegramClick = {}
    )
}

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onUsernameChange: (String) -> Unit,
    onPassChange: (String) -> Unit,
    onLogin: () -> Unit,
    onGoogleClick: () -> Unit,
    onFacebookClick: () -> Unit,
    onTelegramClick: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoginHeaderSection()

        Spacer(modifier = Modifier.height(32.dp))

        LoginFormSection(
            username = uiState.username,
            password = uiState.password,
            onUsernameChange = onUsernameChange,
            onPassChange = onPassChange,
            onLogin = onLogin,
            errorMessage = uiState.errorMessage,
            isLoading = uiState.isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))

        LoginFooterSection(
            onGoogleClick = onGoogleClick,
            onFacebookClick = onFacebookClick,
            onTelegramClick = onTelegramClick,
            onSignUpClick = onSignUpClick
        )
    }
}

@Composable
fun LoginHeaderSection(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(
                    width = 1.dp,
                    color = Color.Black,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.logo_music),
                contentDescription = "Logo Icon",
                modifier = Modifier.size(60.dp)
            )
        }
        Text(
            text = "Get into the Groove",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Welcome back! Sign in to continue",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray
        )
    }
}

@Composable
fun LoginFormSection(
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPassChange: (String) -> Unit,
    onLogin: () -> Unit,
    errorMessage: String?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        TextFieldSection(
            value = username,
            onValueChange = onUsernameChange,
            label = "Username or Email",
            trailingIcon = R.drawable.ic_account,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            enabled = !isLoading
        )

        TextFieldSection(
            value = password,
            onValueChange = onPassChange,
            label = "Password",
            trailingIcon = R.drawable.ic_pass,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            isPassword = true,
            enabled = !isLoading
        )

        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text(
            text = "Forgot Password?",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Magenta,
            modifier = Modifier.align(Alignment.End)
        )

        LoginButton(
            isLoading = isLoading,
            onClick = onLogin
        )
    }
}

@Composable
fun TextFieldSection(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    @DrawableRes trailingIcon: Int,
    keyboardOptions: KeyboardOptions,
    isPassword: Boolean = false,
    enabled: Boolean,
) {
    var passwordVisible = remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        label = { Text(text = label) },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        visualTransformation = if (isPassword && !passwordVisible.value)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = {
                    passwordVisible.value = !passwordVisible.value
                }) {
                    Icon(
                        painter = painterResource(
                            if (passwordVisible.value)
                                R.drawable.ic_visibility
                            else
                                R.drawable.ic_visibility_off
                        ),
                        contentDescription = null
                    )
                }
            } else {
                Icon(
                    painter = painterResource(trailingIcon),
                    contentDescription = null
                )
            }
        },
        shape = RoundedCornerShape(50),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.Gray,
            focusedBorderColor = Color.Black
        )
    )
}

@Composable
fun LoginButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(50)
            )
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF9C27B0),
                        Color(0xFFFF5FD8)
                    )
                ),
                shape = RoundedCornerShape(50)
            )
            .clickable(
                enabled = !isLoading,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isLoading) "Loading..." else "LOG IN",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun LoginFooterSection(
    onGoogleClick: () -> Unit,
    onFacebookClick: () -> Unit,
    onTelegramClick: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(
                modifier = Modifier
                    .weight(1f),
                thickness = 1.dp,
                color = Color.LightGray
            )
            Text(
                text = "Or continue with",
                modifier = Modifier.padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Divider(
                modifier = Modifier
                    .weight(1f),
                thickness = 1.dp,
                color = Color.LightGray
            )
        }
        SocialLogin(
            onGoogleClick = onGoogleClick,
            onFacebookClick = onFacebookClick,
            onTelegramClick = onTelegramClick,
        )
        Row() {
            Text(
                text = "Don't have an account?",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Sign Up",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Magenta,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onSignUpClick() }
            )
        }
    }
}

@Composable
fun SocialLogin(
    onGoogleClick: () -> Unit,
    onFacebookClick: () -> Unit,
    onTelegramClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SocialLoginItem(
            iconRes = R.drawable.ic_google,
            contentDescription = "Google Login",
            onClick = onGoogleClick
        )
        SocialLoginItem(
            iconRes = R.drawable.ic_face,
            contentDescription = "Facebook Login",
            onClick = onFacebookClick
        )
        SocialLoginItem(
            iconRes = R.drawable.ic_tele,
            contentDescription = "Telegram Login",
            onClick = onTelegramClick
        )
    }
}

@Composable
fun SocialLoginItem(
    @DrawableRes iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(52.dp)
            .shadow(
                elevation = 6.dp,
                shape = CircleShape
            )
            .clip(CircleShape)
            .background(Color.White)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    LoginScreen(
        uiState = LoginUiState(),
        onUsernameChange = {},
        onPassChange = {},
        onLogin = {},
        onGoogleClick = {},
        onFacebookClick = {},
        onTelegramClick = {},
        onSignUpClick = {}
    )
}

