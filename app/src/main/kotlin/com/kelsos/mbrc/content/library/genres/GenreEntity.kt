package com.kelsos.mbrc.content.library.genres

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(
    tableName = "genre",
    indices = [(Index("genre", name = "genre_genre_idx", unique = true))]
)
data class GenreEntity(
  @ColumnInfo
  override var genre: String = "",
  @ColumnInfo(name = "date_added")
  var dateAdded: Long = 0,
  @PrimaryKey(autoGenerate = true)
  override var id: Long = 0
) : Genre