package com.kelsos.mbrc.features.library.albums

data class Album(
  var id: Long,
  var artist: String,
  var album: String,
  var albumArtist: String,
  var genre: String,
  var sortableYear: String,
  var dateAdded: Long
)