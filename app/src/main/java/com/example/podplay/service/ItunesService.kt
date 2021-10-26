package com.example.podplay.service

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesService {


companion object{
    val instance : ItunesService by lazy {
        val retrofit= Retrofit.Builder()
                .baseUrl("https://itunes.apple.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        retrofit.create(ItunesService::class.java)
    }
}





     @GET("/search?media=podcast")
    suspend fun getPodcastBySearch(@Query("term") term : String) :
            Response<PodcastResponse>
}