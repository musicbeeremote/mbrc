package com.kelsos.mbrc.content.playlists

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(
    tableName = "playlists",
    indices = [(Index("name", name = "playlist_name_idx", unique = true))]
)
data class PlaylistEntity(
  @ColumnInfo(name = "name")
  override var name: String = "",
  @ColumnInfo(name = "url")
  override var url: String = "",
  @ColumnInfo(name = "dated_added")
  var dateAdded: Long = 0,
  @PrimaryKey(autoGenerate = true)
  override var id: Long = 0
) : Playlist