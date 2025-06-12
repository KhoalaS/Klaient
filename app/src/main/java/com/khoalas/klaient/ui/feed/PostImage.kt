package com.khoalas.klaient.ui.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

@Composable
fun PostImage(imageUrl: String, onClick: () -> Unit) {
    AsyncImage(
        contentScale = ContentScale.FillWidth,
        model = imageUrl,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)

    )
}