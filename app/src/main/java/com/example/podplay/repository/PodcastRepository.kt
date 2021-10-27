package com.example.podplay.repository

import com.example.podplay.model.Podcast

class PodcastRepository {


    fun getPodcast(feedUrl : String)  : Podcast? {
        return Podcast(feedUrl, "no name", " no descreption", "no image") }
}