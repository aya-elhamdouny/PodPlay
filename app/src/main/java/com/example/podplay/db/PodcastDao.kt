package com.example.podplay.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.podplay.model.Episode
import com.example.podplay.model.Podcast


@Dao
interface PodcastDao {


    @Query("SELECT * FROM Podcast ORDER BY FeedTitle ")
    fun loadPodcasts() : LiveData<List<Podcast>>

    @Query("SELECT * FROM Episode WHERE podcastId = :podcastId ORDER BY releaseDate DESC")
    suspend fun loadEpisodes() : List<Episode>


    @Insert(onConflict = REPLACE)
    suspend fun insertPodcast(podcast: Podcast) : Long

    @Insert(onConflict = REPLACE)
    suspend fun insertEpisode(episode: Episode) : Long


}