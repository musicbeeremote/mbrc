package com.kelsos.mbrc.content.library.albums

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(
  tableName = "album",
  indices = [Index("artist", "album", name = "album_info_idx", unique = true)]
)
data class AlbumEntity(
  @ColumnInfo
  override var artist: String,
  @ColumnInfo
  override var album: String,
  @ColumnInfo(name = "date_added")
  var dateAdded: Long = 0,
  @PrimaryKey(autoGenerate = true)
  override var id: Long = 0
) : Album