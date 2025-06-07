package com.khoalas.klaient.screens

import androidx.activity.compose.LocalActivity
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.media3.ui.compose.state.rememberPresentationState
import com.khoalas.klaient.ui.player.Player
import com.khoalas.klaient.ui.player.state.rememberPlayerControlVisibility

@OptIn(UnstableApi::class)
@Composable
fun FullscreenVideoScreen(modifier: Modifier, player: ExoPlayer, onExit: () -> Unit) {
    val controlState = rememberPlayerControlVisibility()

    HideSystemBars()

    Box(
        modifier.background(Color.Black)
    ) {


        Player(
            modifier = Modifier
                .fillMaxSize(),
            player = player,
            onFullscreen = {
            },
            showControls = controlState.showControls,
            onUserInteraction = controlState.onUserInteraction,
            toggleControls = controlState.toggleControls
        )
        AnimatedVisibility(
            visible = controlState.showControls,
            enter = fadeIn(),
            exit = fadeOut(),
        ){
            IconButton(
                onClick = { onExit() },
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Exit fullscreen",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun HideSystemBars() {
    val activity = LocalActivity.current ?: return

    val window = activity.window
    WindowCompat.setDecorFitsSystemWindows(window, false)
    val controller = WindowInsetsControllerCompat(window, window.decorView)

    controller.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

    controller.hide(WindowInsetsCompat.Type.systemBars())

    DisposableEffect(Unit) {
        onDispose {
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}