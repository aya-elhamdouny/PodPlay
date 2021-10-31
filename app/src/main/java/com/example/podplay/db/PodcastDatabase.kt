package com.example.podplay.db

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.room.*
import com.example.podplay.model.Episode
import com.example.podplay.model.Podcast
import kotlinx.coroutines.CoroutineScope
import java.util.*


@Database(entities = [Podcast::class, Episode::class] , version = 1)
@TypeConverters(PodcastDatabase.Converters::class)
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


    class Converters{
        @TypeConverter
        fun fromTimeStamp(value : Long?) : Date?{
          return if (value!=null) null else Date(value)
        }

        @TypeConverter
        fun toTimestamp(date: Date?): Long? {
            return (date?.time)
        }














    }
}