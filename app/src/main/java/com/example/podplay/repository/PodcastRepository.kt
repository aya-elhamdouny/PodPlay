package com.example.podplay.repository

import com.example.podplay.model.Episode
import com.example.podplay.model.Podcast
import com.example.podplay.model.RssFeedResponse
import com.example.podplay.service.RssFeedService
import com.example.podplay.utils.DateUtils

class PodcastRepository(private var feedService: RssFeedService) {

    private fun rssItemsToEpisodes(
        episodeResponses: List<RssFeedResponse.EpisodeResponse>
    ): List<Episode> {
        return episodeResponses.map {
            Episode(

                it.guid ?: "",
                null,
                it.title ?: "",
                it.description ?: "",
                it.url ?: "",
                it.type ?: "",
                DateUtils.xmlDateToDate(it.pubDate),
                        it.duration ?: ""
            )
        }
    }
    private fun rssResponseToPodcast(
        feedUrl: String, imageUrl: String, rssResponse:
        RssFeedResponse
    ): Podcast? {
        val items = rssResponse.episodes ?: return null
        val description = if (rssResponse.description == "")
            rssResponse.summary else rssResponse.description
        return Podcast( null, feedUrl, rssResponse.title, description,
            imageUrl,
            rssResponse.lastUpdated, episodes =
            rssItemsToEpisodes(items))
    }





    suspend fun getPodcast(feedUrl : String)  : Podcast? {
        var podcast: Podcast? = null
        val feedResponse = feedService.getFeed(feedUrl)
        if (feedResponse != null) {
            podcast = rssResponseToPodcast(feedUrl, "", feedResponse)
        }
        return podcast
    }
}