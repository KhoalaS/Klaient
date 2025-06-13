package com.khoalas.klaient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.khoalas.klaient.screens.FeedScreen
import com.khoalas.klaient.screens.FullscreenVideoScreen
import com.khoalas.klaient.ui.theme.KlaientTheme
import com.khoalas.klaient.viewmodel.ActivityViewModel
import com.khoalas.klaient.viewmodel.FeedViewModel
import kotlinx.coroutines.launch
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.net.URLDecoder
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier,
    activityViewModel: ActivityViewModel
) {
    NavHost(
        navController,
        startDestination = startDestination.route,
    ) {
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
                        FeedScreen(feedViewModel, modifier, onFullscreen = { uri ->
                            activityViewModel.setStatusBarHidden(true)
                            val encodedUri = URLEncoder.encode(uri, "UTF-8")
                            navController.navigate("fullscreen/$encodedUri")
                        })
                    }

                    Destination.SEARCH -> {
                        Text(destination.label)
                    }
                }
            }
        }
        composable(
            "fullscreen/{uri}",
        ) { backStackEntry ->
            val uri = backStackEntry.arguments?.getString("uri")
            val decodedUri = if (uri != null) URLDecoder.decode(uri, "UTF-8") else null

            val player: ExoPlayer = koinInject<ExoPlayer>()

            val screenModifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.statusBars.asPaddingValues())

            FullscreenVideoScreen(modifier = screenModifier, player = player, onExit = {
                navController.popBackStack()
                activityViewModel.setStatusBarHidden(false)
            }, downloadUri = decodedUri)
        }
    }
}


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val activityViewModel: ActivityViewModel by viewModels()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                activityViewModel.isStatusBarHidden.collect { hidden ->
                    if (hidden) {
                        WindowInsetsControllerCompat(window, window.decorView)
                            .hide(WindowInsetsCompat.Type.statusBars())
                    } else {
                        WindowInsetsControllerCompat(window, window.decorView)
                            .show(WindowInsetsCompat.Type.statusBars())
                    }
                }
            }
        }


        enableEdgeToEdge()

        this.window.isNavigationBarContrastEnforced = false


        setContent {
            KoinContext {
                KlaientTheme {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            if (currentRoute in BottomTabDestinations.map { it.route }) {
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
                            modifier = Modifier.padding(contentPadding),
                            activityViewModel = activityViewModel
                        )
                    }
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