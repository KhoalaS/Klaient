package com.khoalas.klaient.ui.player

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.media3.ui.compose.state.rememberPresentationState
import com.khoalas.klaient.ui.noRippleClickable

@OptIn(UnstableApi::class)
@Composable
fun Player(
    modifier: Modifier,
    player: ExoPlayer,
    onFullscreen: () -> Unit,
    showControls: Boolean,
    onUserInteraction: () -> Unit,
    toggleControls: () -> Unit
) {

    val presentationState = rememberPresentationState(player)
    val scaledModifier =
        Modifier.resizeWithContentScale(ContentScale.Fit, presentationState.videoSizeDp)


    Box(modifier = modifier) {
        PlayerSurface(
            player = player,
            modifier = scaledModifier.noRippleClickable {
                if (showControls) {
                    toggleControls()
                } else {
                    onUserInteraction()
                }
            }
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