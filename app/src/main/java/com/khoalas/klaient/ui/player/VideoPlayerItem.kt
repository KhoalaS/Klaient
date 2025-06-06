package com.khoalas.klaient.ui.player

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
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
fun VideoPlayerItem(videoUri: String, player: ExoPlayer, onStop: () -> Unit) {
    var showControls by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val hideControlsJob = remember { mutableStateOf<Job?>(null) }
    val hideDelayMillis: Long = 2000

    val presentationState = rememberPresentationState(player)
    var videoSizeDp by remember { mutableStateOf<Size?>(null) }
    // Holds the video size once available

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

    DisposableEffect(videoUri) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (player.playbackState == Player.STATE_READY) {
                    // Now we can safely read the video size
                    presentationState.videoSizeDp?.let {
                        videoSizeDp = presentationState.videoSizeDp
                    }
                }
            }
        }
        player.addListener(listener)
        player.setMediaItem(MediaItem.fromUri(videoUri))
        player.prepare()
        player.play()
        onUserInteraction()

        // VideoPlayerItem leaves the composition
        onDispose {
            player.removeListener(listener)
            player.stop()
            onStop()
        }
    }

    val baseModifier = Modifier.fillMaxWidth().aspectRatio(16f/9f)
    // Important if thumbnail and video have different aspect ratio
    val scaledModifier = videoSizeDp?.let {
        Modifier.resizeWithContentScale(ContentScale.Fit, videoSizeDp)
    } ?: baseModifier


    Box(modifier = scaledModifier) {
        PlayerSurface(
            player = player,
            modifier = scaledModifier.noRippleClickable { showControls = !showControls }
        )
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            MinimalControls(player, modifier = Modifier.fillMaxSize()
                .align(Alignment.Center), onClick = {
                onUserInteraction()
            })
        }
    }
}