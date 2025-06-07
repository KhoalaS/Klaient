package com.khoalas.klaient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.khoalas.klaient.screens.FeedScreen
import com.khoalas.klaient.screens.FullscreenVideoScreen
import com.khoalas.klaient.ui.theme.KlaientTheme
import com.khoalas.klaient.viewmodel.FeedViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController,
        startDestination = startDestination.route,
        modifier = modifier,
    ) {
        val screenModifier = Modifier.fillMaxSize()
        Destination.entries.forEach { destination ->
            composable(destination.route) { backstackEntry ->
                when (destination) {
                    Destination.HOME, Destination.TRENDING, Destination.SAVED -> {
                        val feedViewModel = koinViewModel<FeedViewModel>(
                            viewModelStoreOwner = backstackEntry,
                            parameters = {
                                parametersOf(
                                    destination.apiRoute
                                )
                            }
                        )
                        FeedScreen(feedViewModel, screenModifier, onFullscreen = { uri ->
                            navController.navigate("fullscreen")
                        })
                    }

                    Destination.SEARCH -> {
                        Text(destination.label)
                    }
                }
            }
        }
        composable("fullscreen") { backStackEntry ->
            val player: ExoPlayer = koinInject<ExoPlayer>()
            FullscreenVideoScreen(player = player, onExit = {
                navController.popBackStack()
            })
        }
    }
}


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            KlaientTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if(currentRoute in BottomTabDestinations.map { it.route } ){
                            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                                BottomTabDestinations.forEach { destination ->
                                    val selected = currentRoute == destination.route
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = {
                                            if (!selected) {
                                                navController.navigate(destination.route) {
                                                    launchSingleTop = true
                                                    restoreState = true
                                                    popUpTo(navController.graph.startDestinationId) {
                                                        saveState = true
                                                    }
                                                }
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                destination.icon,
                                                contentDescription = destination.contentDescription
                                            )
                                        },
                                        label = { Text(destination.label) }
                                    )
                                }
                            }
                        }
                    }
                ) { contentPadding ->
                    AppNavHost(
                        navController = navController,
                        startDestination = Destination.HOME,
                        modifier = Modifier.padding(contentPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KlaientTheme {
        Greeting("Android")
    }
}