package com.kelsos.mbrc.core.data.library.album

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import okio.ByteString.Companion.encodeUtf8

@Immutable
data class Album(val id: Long, val artist: String, val album: String, val cover: String?)

@Entity(
  tableName = "album",
  indices = [Index("artist", "album", name = "album_info_idx", unique = true)]
)
data class AlbumEntity(
  @ColumnInfo
  val artist: String,
  @ColumnInfo
  val album: String,
  @ColumnInfo
  val cover: String? = null,
  @ColumnInfo(name = "date_added")
  val dateAdded: Long = 0,
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0
)

fun Album.key(): String = "${artist}_$album".encodeUtf8().sha1().hex().uppercase()
