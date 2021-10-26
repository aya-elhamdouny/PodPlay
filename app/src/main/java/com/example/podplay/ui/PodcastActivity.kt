package com.example.podplay.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.podplay.R
import com.example.podplay.repository.ItunesRepository
import com.example.podplay.service.ItunesService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class PodcastActivity : AppCompatActivity() {

    val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_podcast)

        val itunesService = ItunesService.instance
        val repository = ItunesRepository(itunesService)

        GlobalScope.launch{
            val result  = repository.searchByTerm("Android developer")
            Log.i(TAG , "RESULT = ${result.body()}")
        }


    }
}