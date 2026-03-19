package com.example.musicapp.navigation

import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.musicapp.ui.login_out.login.LoginRoute
import com.example.musicapp.ui.login_out.register.RegisterRoute

fun androidx.navigation.NavGraphBuilder.authNavGraph(
    navController: NavHostController
) {
    composable(AppDestination.Login.route) {
        LoginRoute(
            onLoginSuccess = {
            },
            onSignUpClick = {
                navController.navigate(AppDestination.Register.route)
            }
        )
    }

    composable(AppDestination.Register.route) {
        RegisterRoute(
            onRegisterSuccess = {
                navController.popBackStack()
            },
            onLoginClick = {
                navController.navigate(AppDestination.Login.route)
            }
        )
    }
}
