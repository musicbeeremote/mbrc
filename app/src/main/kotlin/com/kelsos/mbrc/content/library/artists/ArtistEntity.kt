package com.kelsos.mbrc.content.library.artists

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(
  tableName = "artist",
  indices = [(Index("artist", name = "artist_artist_idx", unique = true))]
)
data class ArtistEntity(
  @ColumnInfo
  override var artist: String,
  @ColumnInfo(name = "date_added")
  var dateAdded: Long = 0,
  @PrimaryKey(autoGenerate = true)
  override var id: Long = 0
) : Artist