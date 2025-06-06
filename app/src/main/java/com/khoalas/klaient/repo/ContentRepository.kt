package com.khoalas.klaient.repo

import com.khoalas.klaient.data.model.Post
import com.khoalas.klaient.services.Route
import com.khoalas.klaient.services.ContentService

class ContentRepository(private val contentService: ContentService) {
    suspend fun getPosts(route: Route, page: Int) : List<Post> {
        return contentService.getPosts(route, page).posts
    }

    suspend fun savePost(postId: String) : Boolean {
        return contentService.savePost(postId)
    }
}