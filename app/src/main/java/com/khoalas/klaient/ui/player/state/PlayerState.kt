package com.khoalas.klaient.ui.player.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class PlayerControlState(
    val showControls: Boolean,
    val onUserInteraction: () -> Unit,
    val toggleControls: () -> Unit
)

@Composable
fun rememberPlayerControlVisibility(
    hideDelayMillis: Long = 2000L
): PlayerControlState {
    var showControls by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    var hideJob by remember { mutableStateOf<Job?>(null) }

    fun resetAutoHideTimer() {
        hideJob?.cancel()
        hideJob = scope.launch {
            delay(hideDelayMillis)
            showControls = false
        }
    }

    fun onUserInteraction() {
        showControls = true
        resetAutoHideTimer()
    }

    LaunchedEffect(Unit) {
        onUserInteraction()
    }

    return rememberUpdatedState(
        PlayerControlState(
            showControls = showControls,
            onUserInteraction = ::onUserInteraction,
            toggleControls = {
                showControls = !showControls
                if (showControls) resetAutoHideTimer()
                else hideJob?.cancel()
            }
        )
    ).value
}

