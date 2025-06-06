package com.khoalas.klaient.data.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class Video(
    val url: String,
    val thumbnail: String,
    val isExtended: Boolean,
    val extendedUrl: String,
)
