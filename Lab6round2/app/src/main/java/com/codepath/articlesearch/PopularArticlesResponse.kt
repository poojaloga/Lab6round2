package com.codepath.articlesearch

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PopularArticlesResponse(
    @SerialName("results")
    val results: List<PopularArticle>
)

@Serializable
data class PopularArticle(
    @SerialName("title")
    val title: String,
    @SerialName("abstract")
    val abstract: String? = null, // Optional field
    @SerialName("byline")
    val byline: String? = null, // Optional field
    @SerialName("published_date")
    val publishedDate: String? = null, // Optional field
    @SerialName("media")
    val media: List<Media> = emptyList()
) {
    val mediaImageUrl: String
        get() = media.firstOrNull()?.mediaMetadata?.firstOrNull()?.url ?: "" // Safely handle missing media
}

@Serializable
data class Media(
    @SerialName("media-metadata")
    val mediaMetadata: List<MediaMetadata> = emptyList() // Default to empty list
)

@Serializable
data class MediaMetadata(
    @SerialName("url")
    val url: String
)

