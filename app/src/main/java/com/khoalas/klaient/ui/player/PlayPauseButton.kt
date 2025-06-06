package com.khoalas.klaient.ui.player

import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.rememberPlayPauseButtonState
import com.khoalas.klaient.R

@OptIn(UnstableApi::class)
@Composable
fun PlayPauseButton(player: Player, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val state = rememberPlayPauseButtonState(player)
    val icon = if (state.showPlay) Icons.Default.PlayArrow else Icons.Default.Pause

    val contentDescription =
        if (state.showPlay) stringResource(R.string.playpause_button_play)
        else stringResource(R.string.playpause_button_pause)

    Icon(
        icon,
        contentDescription = contentDescription,
        modifier = modifier.clickable(enabled = state.isEnabled, onClick = {
            state.onClick()
            onClick()
        }),
    )
}