package com.khoalas.klaient.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun VideoThumbnail(
    imageUrl: String,
    onClick: () -> Unit,
) {
    Box {
        AsyncImage(
            contentScale = ContentScale.FillWidth,
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
        )

        Icon(
            Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier
                .size(48.dp)
                .background(Color.Gray.copy(alpha = 0.1f), CircleShape)
                .align(Alignment.Center)
                .clickable(onClick = onClick), tint = Color.White
        )

    }

}