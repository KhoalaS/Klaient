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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.khoalas.klaient.services.DownloadService
import com.khoalas.klaient.ui.noRippleClickable
import com.khoalas.klaient.ui.player.Player
import com.khoalas.klaient.ui.player.state.rememberPlayerControlVisibility
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.util.UUID

@OptIn(UnstableApi::class)
@Composable
fun FullscreenVideoScreen(
    modifier: Modifier,
    player: ExoPlayer,
    downloadUri: String?,
    onExit: () -> Unit
) {
    val controlState = rememberPlayerControlVisibility()
    val downloadService = koinInject<DownloadService>()
    val coroutineScope = rememberCoroutineScope()

    HideSystemBars()

    Box(
        modifier
            .background(Color.Black)
            .noRippleClickable(onClick = {
                controlState.toggleControls()
            })
    )
    {
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
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
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

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            downloadUri?.let {
                                downloadService.downloadToDCIM(
                                    downloadUri,
                                    UUID.randomUUID().toString()
                                )
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        Icons.Default.Download,
                        contentDescription = "Download Video",
                        tint = Color.White
                    )
                }
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