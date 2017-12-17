package com.kelsos.mbrc.content.nowplaying

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(
    tableName = "now_playing",
    indices = [Index("path", "position", name = "now_playing_track_idx", unique = true)]
)
data class NowPlayingEntity(
    @ColumnInfo(name = "title")
    override var title: String = "",
    @ColumnInfo(name = "artist")
    override var artist: String = "",
    @ColumnInfo(name = "path")
    override var path: String = "",
    @ColumnInfo(name = "position")
    override var position: Int = 0,
    @ColumnInfo(name = "date_added")
    var dateAdded: Long = 0,
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0
) : NowPlaying
