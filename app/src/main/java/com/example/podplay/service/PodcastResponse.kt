package com.example.podplay.service


data class PodcastResponse(
    val resultCount: Int,
    val results: List<Result>
){
    data class Result(
        val artistName: String,
        val artworkUrl30: String,
        val collectionCensoredName: String,
        val collectionId: Int,
        val collectionName: String,
        val collectionPrice: Double,
        val country: String,
        val currency: String,
        val feedUrl: String,
        val kind: String,
        val releaseDate: String,
        val trackName: String,
    )

}