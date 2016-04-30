package com.kelsos.mbrc.domain

data class TrackInfo(var album: String, var artist: String, var path: String, var title: String, var year: String) {

  constructor() : this("", "", "", "", "")

  fun isEmpty(): Boolean {
    return album.isNullOrEmpty()
        && artist.isNullOrEmpty()
        && path.isNullOrEmpty()
        && title.isNullOrEmpty()
        && year.isNullOrEmpty()
  }
}
