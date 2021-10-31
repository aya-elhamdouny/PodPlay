package com.example.podplay.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.podplay.model.Podcast
import com.example.podplay.repository.ItunesRepository
import com.example.podplay.model.PodcastResponse
import com.example.podplay.utils.DateUtils

class SearchViewmodel(application: Application) : AndroidViewModel(application) {

    var itunesRepository : ItunesRepository? = null

    data class podcastSummuryViewData(
       var name : String? = "",
       var lastupdated : String? = "",
       var imageUrl : String? = "",
       var feedUrl : String? = ""
    )

    private fun podcastViewToSummeryView(itunesPodcast : PodcastResponse.Result) :
    podcastSummuryViewData{
        return podcastSummuryViewData(
            itunesPodcast.collectionCensoredName,
            DateUtils.jsonDateToShortDate(itunesPodcast.releaseDate),
            itunesPodcast.artworkUrl30,
            itunesPodcast.feedUrl
        )



    }






    suspend fun searchPodcast(term : String) : List<podcastSummuryViewData>{
        val result = itunesRepository?.searchByTerm(term)
        if(result != null && result.isSuccessful)
        {
            val podcast = result.body()?.results
            if(!podcast.isNullOrEmpty()){
                return podcast.map {  podcast->
                    podcastViewToSummeryView(podcast)
                }
            }
           }

        return emptyList()

    }

}