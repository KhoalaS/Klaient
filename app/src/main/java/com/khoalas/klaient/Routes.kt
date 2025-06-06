package com.khoalas.klaient

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.khoalas.klaient.services.Route

enum class Destination(
    val route: String,
    val icon: ImageVector,
    val contentDescription: String,
    val label: String,
    val apiRoute: Route
) {
    HOME("home", Icons.Default.Home, "Home", "Home", Route.HOME),
    TRENDING("trending", Icons.AutoMirrored.Filled.TrendingUp, "Trending", "Trending", Route.TRENDING),
    SAVED("saved", Icons.Default.Favorite, "Saved", "Saved", Route.SAVED),
    SEARCH("search", Icons.Default.Search, contentDescription = "Search", "Search", Route.NONE)
}

val BottomTabDestinations = listOf(Destination.HOME, Destination.TRENDING, Destination.SAVED, Destination.SEARCH)