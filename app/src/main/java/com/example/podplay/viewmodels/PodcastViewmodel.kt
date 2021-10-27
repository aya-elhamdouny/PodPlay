package com.example.podplay.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.podplay.model.Episode
import com.example.podplay.model.Podcast
import com.example.podplay.repository.PodcastRepository
import java.util.*

class PodcastViewmodel(application: Application) : AndroidViewModel(application) {


    var repo : PodcastRepository? = null
    var episodeViewData : EpisodeViewData? = null
    var activepodcastViewData : PodcastViewData? = null

    private fun episodeToEpisodeView(episodes: List<Episode>) :
      List<EpisodeViewData>{
        return  episodes.map {
            EpisodeViewData(
                    it.guid,
                    it.title,
                    it.description,
                    it.mediaUrl,
                    it.releaseDate,
                    it.duration
            )
        }
    }


    private fun podcastToPodcastView(podcast: Podcast) : PodcastViewData{
        return PodcastViewData(
                false,
                podcast.feedUrl,
                podcast.feedTitle,
                podcast.feedDesc,
                podcast.imageUrl,
                episodeToEpisodeView(podcast.episodes)
        )
    }


    data class EpisodeViewData (
            var guid: String = "",
            var title: String = "",
            var description: String = "",
            var mediaUrl: String = "",
            var releaseDate: Date = Date(),
            var duration: String = ""
    )

    data class PodcastViewData(
            var subscribed: Boolean = false,
            var feedUrl: String = "",
            var feedTitle: String = "",
            var feedDesc: String = "",
            var imageUrl: String = "",
            var episodes: List<EpisodeViewData> = listOf()
    )


    fun getEpisode(podcastViewData : SearchViewmodel.podcastSummuryViewData) : PodcastViewData?{
        val result = podcastViewData.feedUrl?.let { repo?.getPodcast(it) }
        result?.let {
            it.feedTitle = podcastViewData.name ?:""
            it.imageUrl = podcastViewData.imageUrl ?:""
            activepodcastViewData = podcastToPodcastView(it)
            return  activepodcastViewData
        }
              return null
    }




}