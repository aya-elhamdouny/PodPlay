package com.example.podplay.db

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.podplay.model.Episode
import com.example.podplay.model.Podcast
import kotlinx.coroutines.CoroutineScope


@Database(entities = [Podcast::class, Episode::class] , version = 1)
abstract class PodcastDatabase :RoomDatabase(){


    abstract fun podcastDao(): PodcastDao

    companion object{
        private var INSTACE: PodcastDatabase? = null
          fun getInstance(context: Context , coroutineScope: CoroutineScope) : PodcastDatabase{
              val temp = INSTACE
              if(temp !=null){
                  return temp
              }
              synchronized(this){
                  val instance =
                      Room.databaseBuilder(context.applicationContext , PodcastDatabase::class.java , "Podplay")
                          .build()
                  INSTACE= instance
                  return instance
              }



          }





    }
}