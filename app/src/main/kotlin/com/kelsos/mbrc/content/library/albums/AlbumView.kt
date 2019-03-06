package com.kelsos.mbrc.content.library.albums

import androidx.room.DatabaseView

@DatabaseView("""
    select distinct
        album.album,
        album.artist,
        track.album_artist,
        track.sortable_year,
        track.genre,
        album.id,
        album.date_added
    from album
    inner join track on album.album = track.album
    and album.artist = track.album_artist
""")
data class AlbumView(
  val album: String,
  val artist: String,
  val albumArtist: String,
  val sortableYear: String,
  val genre: String,
  val dateAdded: Long,
  val id: Long
)