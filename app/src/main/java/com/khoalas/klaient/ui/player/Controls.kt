package com.khoalas.klaient.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.media3.common.Player
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

/**
 * Minimal playback controls for a [Player].
 *
 * Includes buttons for seeking to a previous/next items or playing/pausing the playback.
 */
@Composable
fun MinimalControls(
    player: Player,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onFullscreen: () -> Unit
) {
    val graySemiTransparentBackground = Color.Gray.copy(alpha = 0.1f)
    val modifierForIconButton =
        Modifier
            .size(48.dp)
            .background(graySemiTransparentBackground, CircleShape)
    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.4f))
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlayPauseButton(player, modifierForIconButton, onClick)
        }
        IconButton(modifier = Modifier.align(Alignment.BottomEnd), onClick = {
            onFullscreen()
            onClick()
        }) {
            Icon(Icons.Default.Fullscreen, contentDescription = null)
        }
    }
}