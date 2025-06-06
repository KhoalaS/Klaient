package com.khoalas.klaient.services.data

import com.khoalas.klaient.data.model.Post
import kotlinx.serialization.Serializable

@Serializable
data class FeedResponse(val next: Int, val posts: List<Post>)
