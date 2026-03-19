package com.example.musicapp.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.musicapp.R

sealed class BottomBarDestination(
    val route: String,
    @StringRes val label: Int,
    @DrawableRes val icon: Int
) {
    object Home : BottomBarDestination(
        route = AppDestination.Home.route,
        label = R.string.home,
        icon = R.drawable.ic_home
    )
    object Library : BottomBarDestination(
        route = AppDestination.Library.route,
        label = R.string.library,
        icon = R.drawable.ic_library
    )
    object Settings : BottomBarDestination(
        route = AppDestination.Settings.route,
        label = R.string.settings,
        icon = R.drawable.ic_settings
    )
}

val bottomBarDestinations = listOf(
    BottomBarDestination.Home,
    BottomBarDestination.Library,
    BottomBarDestination.Settings
)