package com.example.podplay.repository

import com.example.podplay.service.ItunesService

class ItunesRepository(private val service: ItunesService) {

    suspend fun searchByTerm(term : String) =
        service.getPodcastBySearch(term)


}