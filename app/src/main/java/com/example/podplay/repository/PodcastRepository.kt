package com.example.podplay.repository

import androidx.lifecycle.LiveData
import com.example.podplay.db.PodcastDao
import com.example.podplay.model.Episode
import com.example.podplay.model.Podcast
import com.example.podplay.model.RssFeedResponse
import com.example.podplay.service.RssFeedService
import com.example.podplay.utils.DateUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PodcastRepository(private var feedService: RssFeedService , private var dao: PodcastDao) {

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
        val podcastLocal = dao.loadPodcast(feedUrl)
        if (podcastLocal != null) {
            podcastLocal.id?.let {
                podcastLocal.episodes = dao.loadEpisodes(it)
                return podcastLocal
            }
        }
        var podcast: Podcast? = null
        val feedResponse = feedService.getFeed(feedUrl)
        if (feedResponse != null) {
            podcast = rssResponseToPodcast(feedUrl, "", feedResponse)
        }
        return podcast
    }


    fun save(podcast: Podcast){
        GlobalScope.launch {

            val podcastId = dao.insertPodcast(podcast)
            for(episode in podcast.episodes){
                episode.podcastID = podcastId
                dao.insertEpisode(episode)
            }

        }
    }


    fun getAll(): LiveData<List<Podcast>>{
         return dao.loadPodcasts()
    }

    fun delete(podcast: Podcast) {
        GlobalScope.launch {
            dao.deletePodcast(podcast)
        }
    }
}