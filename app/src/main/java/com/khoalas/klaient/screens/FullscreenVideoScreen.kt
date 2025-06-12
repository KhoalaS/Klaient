package com.khoalas.klaient.screens

import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.state.rememberPresentationState
import com.khoalas.klaient.services.DownloadService
import com.khoalas.klaient.ui.noRippleClickable
import com.khoalas.klaient.ui.player.Player
import com.khoalas.klaient.ui.player.state.rememberPlayerControlVisibility
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.util.UUID

@OptIn(UnstableApi::class)
@ExperimentalMaterial3Api
@Composable
fun FullscreenVideoScreen(
    modifier: Modifier,
    player: ExoPlayer,
    downloadUri: String?,
    onExit: () -> Unit
) {
    var shouldShowPlayer by remember { mutableStateOf(true) }
    val presentationState = rememberPresentationState(player)

    val controlState = rememberPlayerControlVisibility()
    val downloadService = koinInject<DownloadService>()
    val coroutineScope = rememberCoroutineScope()

    var position by remember { mutableLongStateOf(0L) }
    val duration = player.duration.coerceAtLeast(0L)
    var buffer by remember { mutableIntStateOf(player.bufferedPercentage) }

    var currentSize = presentationState.videoSizeDp
    val density = LocalDensity.current

// Handle system back press to clean up first
    BackHandler {
        shouldShowPlayer = false
        player.stop()
        onExit()
    }

    // Polling coroutine
    LaunchedEffect(Unit) {
        while (true) {
            position = player.currentPosition
            buffer = player.bufferedPercentage
            delay(500L)
        }
    }

    Box(
        modifier
            .background(Color.Black)
            .noRippleClickable(onClick = {
                controlState.toggleControls()
            })
    )
    {
        if (shouldShowPlayer) {
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
        } else {
            if (currentSize != null) {
                with(density) {
                    Box(
                        modifier = Modifier.size(
                            currentSize.width.toDp(),
                            currentSize.height.toDp(),
                        )
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = controlState.showControls,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                IconButton(
                    onClick = {
                        shouldShowPlayer = false
                        player.stop()
                        onExit()
                    },
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
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = 72.dp, start = 12.dp, end = 12.dp)
                ) {
                    Slider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                        value = position.toFloat(),
                        valueRange = 0f..duration.toFloat(),
                        onValueChange = {
                            player.seekTo(it.toLong().coerceAtLeast(0L))
                            position = it.toLong().coerceAtLeast(0L)
                            controlState.onUserInteraction()
                        },
                        track = {
                            SliderDefaults.Track(sliderState = it, modifier = Modifier.height(4.dp))
                        }
                    )
                }
            }
        }
    }
}
