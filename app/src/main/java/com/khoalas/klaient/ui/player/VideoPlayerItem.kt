package com.khoalas.klaient.ui.player

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.media3.ui.compose.state.rememberPresentationState
import com.khoalas.klaient.ui.player.state.rememberPlayerControlVisibility

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerItem(
    videoUri: String,
    player: ExoPlayer,
    onStop: () -> Unit,
    onFullscreen: (uri: String) -> Unit
) {
    val presentationState = rememberPresentationState(player)
    var videoSizeDp by remember { mutableStateOf<Size?>(null) }
    // Holds the video size once available

    var isFullscreen by remember { mutableStateOf(false) }

    val controlState = rememberPlayerControlVisibility()

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

        // VideoPlayerItem leaves the composition
        onDispose {
            if (!isFullscreen) {
                player.stop()
            }
            player.removeListener(listener)
            onStop()
        }
    }

    val baseModifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16f / 9f)
    // Important if thumbnail and video have different aspect ratio
    val scaledModifier = videoSizeDp?.let {
        Modifier.resizeWithContentScale(ContentScale.Fit, videoSizeDp)
    } ?: baseModifier


    Player(
        modifier = scaledModifier,
        player = player,
        onFullscreen = {
            isFullscreen = !isFullscreen
            onFullscreen(videoUri)
        },
        showControls = controlState.showControls,
        onUserInteraction = controlState.onUserInteraction,
        toggleControls = controlState.toggleControls
    )
}