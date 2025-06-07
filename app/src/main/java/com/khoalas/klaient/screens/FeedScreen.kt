package com.khoalas.klaient.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khoalas.klaient.data.model.PostType
import com.khoalas.klaient.ui.player.VideoThumbnail
import com.khoalas.klaient.ui.player.VideoPlayerItem
import com.khoalas.klaient.viewmodel.FeedViewModel

@Composable
fun FeedScreen(viewModel: FeedViewModel, modifier: Modifier = Modifier, onFullscreen: (uri: String) -> Unit) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }


    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                val totalItems = listState.layoutInfo.totalItemsCount
                // If the user scrolls near the end, load more
                if (lastVisibleIndex != null && lastVisibleIndex >= totalItems - 3 && !state.isLoading && !state.endReached) {
                    viewModel.loadNext()
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        items(state.items, key = { item -> item.postId }) { post ->
            Column(modifier = Modifier.padding(6.dp)) {
                Text(post.title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                if (post.tags.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        post.tags.forEach() { tag ->
                            SuggestionChip(label = {
                                Text(tag)
                            }, onClick = {})
                        }
                    }
                }
                Row {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.onTertiary)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.RemoveRedEye, contentDescription = null)
                        Text("${post.views} Views")
                    }
                }
            }
            if (post.postType == PostType.VIDEO && post.video != null) {
                if (post.video.url == state.currentPlayingVideo?.url) {
                    VideoPlayerItem(
                        videoUri = if (post.video.isExtended) post.video.extendedUrl else post.video.url,
                        player = viewModel.player,
                        onStop = {
                            if (state.currentPlayingVideo?.url == post.video.url) {
                                viewModel.stopPlayback()
                            }
                        },
                        onFullscreen = onFullscreen
                    )
                } else {
                    VideoThumbnail(
                        imageUrl = post.video.thumbnail,
                        onClick = { viewModel.playVideo(post.video) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            ) {
                FilledTonalIconButton(
                    onClick = {
                        viewModel.savePost(post)
                    },
                    modifier = Modifier.width(64.dp)
                ) {
                    Icon(
                        if (post.isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null
                    )
                }
                FilledTonalIconButton(onClick = {}, modifier = Modifier.width(64.dp)) {
                    Icon(Icons.Default.Download, contentDescription = null)
                }

            }
        }
        if (state.isLoading) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}
