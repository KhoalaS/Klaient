package com.khoalas.klaient.services

import com.khoalas.klaient.services.data.SaveResponse
import com.khoalas.klaient.services.data.FeedResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.http.HttpHeaders
import io.ktor.http.appendPathSegments

enum class Route(val path: String) {
    HOME("home"),
    TRENDING("trending"),
    POST_SAVE("save"),
    SAVED("saved"),
    NONE("")
}

class ContentService(private val client: HttpClient) {
    private var baseUrl = "http://192.168.178.41:8086/api/v1"

    suspend fun getPosts(route: Route, page: Int): FeedResponse {
        val result = client.get(baseUrl) {
            url {
                appendPathSegments(route.path)
                parameters.append("page", page.toString())
            }
            expectSuccess = true
            headers {
                append(HttpHeaders.Accept, "application/json")
            }
        }.body<FeedResponse>()
        return result
    }

    suspend fun savePost(postId: String): Boolean {
        val result = client.post(baseUrl) {
            url {
                appendPathSegments(Route.POST_SAVE.path)
                parameters.append("id", postId)
            }
            expectSuccess = true
            headers {
                append(HttpHeaders.Accept, "application/json")
            }
        }.body<SaveResponse>()
        return result.saved
    }
}