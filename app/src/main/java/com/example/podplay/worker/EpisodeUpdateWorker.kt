package com.example.podplay.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.podplay.R
import com.example.podplay.db.PodcastDatabase
import com.example.podplay.repository.PodcastRepository
import com.example.podplay.service.RssFeedService
import com.example.podplay.ui.PodcastActivity
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class EpisodeUpdateWorker(context: Context , params : WorkerParameters) :
    CoroutineWorker(context , params) {

    companion object{
        const val EPISODE_CHANNEL_ID = "podplay_episode_channel"
        const val EXTRA_FEED_URL = "PodcastUrl"
    }


    override suspend fun doWork(): Result = coroutineScope {
        val job = async {
            val db = PodcastDatabase.getInstance(applicationContext  , this)
            val repo = PodcastRepository(RssFeedService.instance , db.podcastDao())

            val podcastUpdates = repo.updatePodcastEpisodes()
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                createNotifactionChannel()
            }

            for(podcastupdate in podcastUpdates ){
                displayNotifaction(podcastupdate)

            }
        }
        job.await()

        Result.success()
    }


    //create notifaction channel
     @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotifactionChannel(){

        val notifactionManger = applicationContext.
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if(notifactionManger.getNotificationChannel(EPISODE_CHANNEL_ID) == null){
            val channel = NotificationChannel(EPISODE_CHANNEL_ID , "Episodes"
            , NotificationManager.IMPORTANCE_DEFAULT)

            notifactionManger.createNotificationChannel(channel)

        }

    }



    private fun displayNotifaction(podcastInfo : PodcastRepository.PodcastUpdateInfo){

        val contentintent = Intent(applicationContext , PodcastActivity::class.java)
        contentintent.putExtra(EXTRA_FEED_URL , podcastInfo.feedUrl)

        val pendingContentIntent = PendingIntent.getActivity(applicationContext ,
            0 , contentintent , PendingIntent.FLAG_UPDATE_CURRENT)

        val notification =
            NotificationCompat.Builder(applicationContext , EPISODE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_play_circle_outline_24)
                .setContentTitle(applicationContext.getString(R.string.episode_notification_title))
                .setContentText(applicationContext.getString(R.string.episode_notification_text ,
                    podcastInfo.newCount , podcastInfo.name))
                .setNumber(podcastInfo.newCount)
                .setAutoCancel(true)
                .setContentIntent(pendingContentIntent)
                .build()


        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(podcastInfo.name , 0 , notification)


    }
}