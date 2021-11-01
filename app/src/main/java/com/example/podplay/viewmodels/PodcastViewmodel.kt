package com.example.podplay.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.podplay.db.Converters
import com.example.podplay.db.PodcastDao
import com.example.podplay.db.PodcastDatabase
import com.example.podplay.model.Episode
import com.example.podplay.model.Podcast
import com.example.podplay.repository.PodcastRepository
import com.example.podplay.utils.DateUtils
import kotlinx.coroutines.launch
import java.util.*

class PodcastViewmodel(application: Application) : AndroidViewModel(application) {


    var repo : PodcastRepository? = null
    var activepodcastViewData : PodcastViewData? = null
    val dao : PodcastDao = PodcastDatabase.getInstance(application , viewModelScope).podcastDao()

     var activePodcast : Podcast? = null

    private val _podcastLiveData = MutableLiveData<PodcastViewData?
            >()
    val podcastLiveData: LiveData<PodcastViewData?> =
        _podcastLiveData


    var livePodcastSummaryData:
            LiveData<List<SearchViewmodel.podcastSummuryViewData>>? = null



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
        return return PodcastViewData(
            podcast.id != null,
            podcast.feedTitle,
            podcast.feedUrl,
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


    suspend fun getPodcast(podcastSummaryViewData: SearchViewmodel.podcastSummuryViewData) {
        podcastSummaryViewData.feedUrl?.let { url ->
            repo?.getPodcast(url)?.let {
                it.feedTitle = podcastSummaryViewData.name ?: ""
                it.imageUrl = podcastSummaryViewData.imageUrl ?: ""
                _podcastLiveData.value = podcastToPodcastView(it)
                activePodcast = it
            } ?: run {
                _podcastLiveData.value = null
            }
        } ?: run {
            _podcastLiveData.value = null
        }
    }
  fun savePoacst(){
      val repo = repo ?: return
      activePodcast?.let {
          repo.save(it)
      }

  }

    private fun podcastToSummaryView(podcast: Podcast):
            SearchViewmodel.podcastSummuryViewData {
        return SearchViewmodel.podcastSummuryViewData(
            podcast.feedTitle,
            DateUtils.dateToShortDate(podcast.lastUpdated),
            podcast.imageUrl,
            podcast.feedUrl
        )
    }
    fun getPodcasts(): LiveData<List<SearchViewmodel.podcastSummuryViewData>>? {
        val repo = repo ?: return null
    if(livePodcastSummaryData != null){
        val livedata = repo.getAll()
        livePodcastSummaryData = Transformations.map(livedata){
            it.map {
                podcastToSummaryView(it)
            }
        }
    }
        return livePodcastSummaryData
    }




    fun deleteActivePodcast() {
        val repo = repo ?: return
        activePodcast?.let {
            repo.delete(it)
        }
    }









}