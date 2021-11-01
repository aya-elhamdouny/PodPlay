package com.example.podplay.db

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.room.*
import androidx.work.impl.WorkDatabaseMigrations.MIGRATION_1_2
import com.example.podplay.model.Episode
import com.example.podplay.model.Podcast
import kotlinx.coroutines.CoroutineScope
import java.util.*



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


@Database(entities = [Podcast::class, Episode::class] , version = 1)
@TypeConverters(Converters::class)
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
                          .allowMainThreadQueries()
                          .fallbackToDestructiveMigration()
                          .build()
                  INSTACE= instance
                  return instance
              }
   }
    }
















    }
