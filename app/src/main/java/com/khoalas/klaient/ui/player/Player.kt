package com.khoalas.klaient.ui.player

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.media3.ui.compose.state.rememberPresentationState
import com.khoalas.klaient.ui.noRippleClickable
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun Player(modifier: Modifier, player: ExoPlayer, onFullscreen: () -> Unit) {
    var showControls by remember { mutableStateOf(true) }
    val hideControlsJob = remember { mutableStateOf<Job?>(null) }
    val scope = rememberCoroutineScope()
    val hideDelayMillis: Long = 2000

    val presentationState = rememberPresentationState(player)
    val scaledModifier = Modifier.resizeWithContentScale(ContentScale.FillWidth, presentationState.videoSizeDp)

    fun resetAutoHideTimer() {
        hideControlsJob.value?.cancel()
        hideControlsJob.value = scope.launch {
            delay(hideDelayMillis)
            showControls = false
        }
    }

    // Reset timer when user interacts
    fun onUserInteraction() {
        showControls = true
        resetAutoHideTimer()
    }

    Box(modifier = modifier) {
        PlayerSurface(
            player = player,
            modifier = scaledModifier.noRippleClickable { showControls = !showControls }
        )
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            MinimalControls(
                player, modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center), onClick = {
                    onUserInteraction()
                }, onFullscreen = {
                    onFullscreen()
                })
        }
    }
}