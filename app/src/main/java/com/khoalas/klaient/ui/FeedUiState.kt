package com.khoalas.klaient.ui

import com.khoalas.klaient.data.model.Video

data class FeedUiState<T> (
    val items: List<T> = emptyList(),
    val isLoading: Boolean = false,
    val endReached: Boolean = false,
    val currentPlayingVideo: Video? = null
)