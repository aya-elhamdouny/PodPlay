package com.example.podplay.service

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat

class PodplaymediaService : MediaBrowserServiceCompat() {

    private lateinit var mediaSession : MediaSessionCompat


    private fun createMediaSession(){
        mediaSession = MediaSessionCompat( this, "PodPlayMediaSession")
        setSessionToken(mediaSession.sessionToken)
        val callback = PodPlayMediaCallBack(this , mediaSession)
        mediaSession.setCallback(callback)


    }

    override fun onCreate() {
        super.onCreate()
        createMediaSession()
    }


    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        TODO("Not yet implemented")
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        TODO("Not yet implemented")
    }
}