package com.kelsos.mbrc.content.library.genres

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

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