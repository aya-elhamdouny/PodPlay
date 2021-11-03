package com.example.podplay.service

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.fragment.app.FragmentActivity

class PodPlayMediaCallBack(
    val context: Context,
    val mediaSession: MediaSessionCompat,
    val mediaPlayer: MediaPlayer? = null
)  : MediaSessionCompat.Callback() {


    override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
        super.onPlayFromUri(uri, extras)
        println("Playing ${uri.toString()}")
        onPlay()
        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI,
                uri.toString())
            .build())
    }

    override fun onPlay() {
        super.onPlay()
        println("onPlay called")
        setState(PlaybackStateCompat.STATE_PLAYING)
    }

    override fun onPause() {
        super.onPause()
       setState(PlaybackStateCompat.STATE_PAUSED)
    }

    override fun onStop() {
        super.onStop()
        println("onStop called")
    }

     private fun setState(state : Int){
         var postion : Long = -1

         val playbackState = PlaybackStateCompat.Builder()
             .setActions(
                 PlaybackStateCompat.ACTION_PLAY or
                         PlaybackStateCompat.ACTION_STOP or
                         PlaybackStateCompat.ACTION_PLAY_PAUSE or
                         PlaybackStateCompat.ACTION_PAUSE)
             .setState(state , postion , 1.0f)
             .build()

         mediaSession.setPlaybackState(playbackState)

     }







}