package com.khoalas.klaient.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val postType: PostType,
    val postId: String,
    val title: String,
    val isSaved: Boolean,
    val tags: List<String> = listOf(),
    val text: String?,
    val image: String?,
    val video: Video?,
    val views: String?,
)

@Serializable
enum class PostType {
    IMAGE,
    TEXT,
    VIDEO,
    GALLERY
}