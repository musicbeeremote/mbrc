package com.kelsos.mbrc.core.data.nowplaying

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Immutable
data class NowPlaying(
  val title: String,
  val artist: String,
  val path: String,
  val position: Int,
  val id: Long
)

@Entity(
  tableName = "now_playing",
  indices = [
    Index(value = ["position"], name = "now_playing_position_idx"),
    Index(value = ["date_added"], name = "now_playing_date_added_idx")
  ]
)
data class NowPlayingEntity(
  @ColumnInfo(name = "title")
  val title: String = "",
  @ColumnInfo(name = "artist")
  val artist: String = "",
  @ColumnInfo(name = "path")
  val path: String = "",
  @ColumnInfo(name = "position")
  val position: Int = 0,
  @ColumnInfo(name = "date_added")
  val dateAdded: Long = 0,
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0
)

data class CachedNowPlaying(val id: Long, val path: String, val position: Int)

data class SearchResult(val position: Int, val title: String)
