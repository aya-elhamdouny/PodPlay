package com.example.podplay.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*


@Entity(
        foreignKeys = [
                ForeignKey(
                        entity = Podcast::class,
                        parentColumns = ["id"],
                        childColumns = ["podcastID"],
                        onDelete = ForeignKey.CASCADE
                )
        ] , indices = [Index("podcastID")]
)
data class Episode (

        @PrimaryKey
        var guid: String = "",
        var podcastID: Long? = null,
        var title: String = "",
        var description: String = "",
        var mediaUrl: String = "",
        var mimeType: String = "",
        var releaseDate: Date = Date(),
        var duration: String = ""
        )