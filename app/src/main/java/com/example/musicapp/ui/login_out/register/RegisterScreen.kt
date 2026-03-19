package com.example.musicapp.ui.login_out.register

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicapp.AppViewModelFactory
import com.example.musicapp.R

@Composable
fun RegisterRoute(
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: RegisterViewModel = viewModel(
        factory = AppViewModelFactory(LocalContext.current)
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    RegisterScreen(
        uiState = uiState,
        onUsernameChange = viewModel::onUsernameChange,
        onPassChange = viewModel::onPassChange,
        onEmailChange = viewModel::onEmailChange,
        onRegisterClick = { viewModel.onRegisterClick(onRegisterSuccess) },
        onTermsClick = viewModel::onTermsClick,
        onLoginClick = onLoginClick,
        onConfirmPassChange = viewModel::onConfirmPassChange
    )

}

@Composable
fun RegisterScreen(
    uiState: RegisterUiState,
    onUsernameChange: (String) -> Unit,
    onPassChange: (String) -> Unit,
    onConfirmPassChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onTermsClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        RegisterHeaderSection()

        Spacer(modifier = Modifier.height(32.dp))

        RegisterFormSection(
            username = uiState.username,
            password = uiState.password,
            email = uiState.email,
            confirmPass = uiState.confirmPass,
            onUsernameChange = onUsernameChange,
            onPassChange = onPassChange,
            onEmailChange = onEmailChange,
            onRegisterClick = onRegisterClick,
            onTermsClick = onTermsClick,
            errorMessage = uiState.errorMessage,
            isLoading = uiState.isLoading,
            isTermsAccepted = uiState.isTermsAccepted,
            onLoginClick = onLoginClick,
            onConfirmPassChange = onConfirmPassChange,
        )
    }
}

@Composable
fun RegisterHeaderSection(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Join the Beat",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Magenta
        )
        Text(
            text = "Create an account to continue",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun RegisterFormSection(
    username: String,
    password: String,
    email: String,
    confirmPass: String,
    onUsernameChange: (String) -> Unit,
    onPassChange: (String) -> Unit,
    onConfirmPassChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    errorMessage: String?,
    isLoading: Boolean,
    isTermsAccepted: Boolean,
    onTermsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextFieldSection(
            value = username,
            onValueChange = onUsernameChange,
            label = "Username",
            placeholder = "e.g.DaiBigDick",
            trailingIcon = null,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            enabled = !isLoading
        )
        TextFieldSection(
            value = email,
            onValueChange = onEmailChange,
            label = "Email Address",
            placeholder = "name@gmail.com",
            trailingIcon = R.drawable.ic_email,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            enabled = !isLoading
        )
        TextFieldSection(
            value = password,
            onValueChange = onPassChange,
            label = "Password",
            placeholder = "Create a strong password",
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            enabled = !isLoading,
            isPassword = true
        )

        TextFieldSection(
            value = confirmPass,
            onValueChange = onConfirmPassChange,
            label = "Confirm Password",
            placeholder = "Repeat Password",
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            enabled = !isLoading,
            isPassword = true
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isTermsAccepted,
                onCheckedChange = if(!isLoading){ { onTermsClick() } } else null
            )
            Text(
                text = buildAnnotatedString {
                    append("I agree to the")
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = Color.Magenta
                        )
                    ) {
                        append(" Terms of Service")
                    }
                    append(" and")
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = Color.Magenta
                        )
                    ) {
                        append(" Privacy Policy")
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        RegisterButton(
            enabled = isTermsAccepted && !isLoading,
            onClick = onRegisterClick,
            isLoading = isLoading,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account?",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Login",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Magenta,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onLoginClick() }
            )
        }
    }
}

@Composable
fun TextFieldSection(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    @DrawableRes trailingIcon: Int? = null,
    keyboardOptions: KeyboardOptions,
    enabled: Boolean,
    isPassword: Boolean = false,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            placeholder = { Text(placeholder) },
            singleLine = true,
            keyboardOptions = keyboardOptions,
            visualTransformation =
                if (isPassword && !passwordVisible)
                    PasswordVisualTransformation()
                else
                    VisualTransformation.None,
            trailingIcon = {
                when {
                    isPassword -> {
                        IconButton(onClick = {
                            passwordVisible = !passwordVisible
                        }) {
                            Icon(
                                painter = painterResource(
                                    if (passwordVisible)
                                        R.drawable.ic_visibility
                                    else
                                        R.drawable.ic_visibility_off
                                ),
                                contentDescription = null
                            )
                        }
                    }
                    trailingIcon != null -> {
                        Icon(
                            painter = painterResource(trailingIcon),
                            contentDescription = null
                        )
                    }
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
}

@Composable
fun RegisterButton(
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientColors =
        if(enabled) {
            listOf(
                Color(0xFF9C27B0),
                Color(0xFFFF5FD8)
            )
        } else {
            listOf(
                Color(0xFF9C27B0).copy(alpha = 0.4f),
                Color(0xFFFF5FD8).copy(alpha = 0.4f)
            )
        }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(50),
                ambientColor = Color(0xFF9C27B0),
                spotColor = Color(0xFFFF5FD8)
            )
            .background(
                brush = Brush.horizontalGradient(gradientColors),
                shape = RoundedCornerShape(50)
            )
            .then(
                if(enabled) Modifier.clickable{ onClick() }
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if(isLoading) "Loading..." else "SIGN UP",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = if (enabled) 1f else 0.6f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    MaterialTheme{
        RegisterScreen(
            uiState = RegisterUiState(),
            onUsernameChange = {},
            onPassChange = {},
            onEmailChange = {},
            onRegisterClick = {},
            onTermsClick = {},
            onLoginClick = {},
            onConfirmPassChange = {}
        )
    }
}

