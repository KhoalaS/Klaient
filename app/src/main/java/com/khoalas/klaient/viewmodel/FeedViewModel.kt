package com.khoalas.klaient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.khoalas.klaient.data.model.Post
import com.khoalas.klaient.data.model.PostType
import com.khoalas.klaient.data.model.Video
import com.khoalas.klaient.repo.ContentRepository
import com.khoalas.klaient.services.DownloadService
import com.khoalas.klaient.services.Route
import com.khoalas.klaient.ui.FeedUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel(
    private val contentRepository: ContentRepository,
    private val downloadService: DownloadService,
    val player: ExoPlayer,
    private val route: Route
) :
    ViewModel() {
    private val _uiState = MutableStateFlow<FeedUiState<Post>>(FeedUiState())
    val uiState: StateFlow<FeedUiState<Post>> = _uiState.asStateFlow()

    private var page = 1

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            val result = contentRepository.getPosts(route, page)
            _uiState.update {
                it.copy(isLoading = false, endReached = result.isEmpty(), items = it.items + result)
            }
            page++
        }
    }

    fun loadNext() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            val result = contentRepository.getPosts(route, page)
            _uiState.update {
                it.copy(isLoading = false, endReached = result.isEmpty(), items = it.items + result)
            }
            page++
        }
    }

    fun playVideo(video: Video) {
        if (_uiState.value.currentPlayingVideo?.url == video.url) {
            return
        }

        _uiState.update {
            it.copy(currentPlayingVideo = video)
        }
    }

    fun stopPlayback() {
        _uiState.update {
            it.copy(currentPlayingVideo = null)
        }
    }

    fun savePost(post: Post) {
        viewModelScope.launch {
            val saved = contentRepository.savePost(post.postId)
            _uiState.update { currentState ->
                val updatedVideos = currentState.items.map { item ->
                    if (post.postId == item.postId) item.copy(isSaved = saved) else item
                }
                currentState.copy(items = updatedVideos)
            }
        }
    }

    fun downloadMedia(post: Post) {
        if(post.postType == PostType.VIDEO) {
            post.video?.let {
                viewModelScope.launch {
                    downloadService.downloadToDCIM(post.video.url, post.title)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}