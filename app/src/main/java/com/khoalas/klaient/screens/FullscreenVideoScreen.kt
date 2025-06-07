package com.khoalas.klaient.screens

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.media3.ui.compose.state.rememberPresentationState
import com.khoalas.klaient.ui.player.Player

@OptIn(UnstableApi::class)
@Composable
fun FullscreenVideoScreen(modifier: Modifier, player: ExoPlayer, onExit: () -> Unit) {
    var showControls by remember { mutableStateOf(true) }
    Box(
        modifier.background(Color.Black)
    ) {
        Player(
            modifier = Modifier
                .fillMaxSize(), player = player, onFullscreen = {}, onShowControls = {
                    showControls = it
            })
        AnimatedVisibility(
            visible = showControls,
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