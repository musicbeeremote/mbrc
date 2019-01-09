package com.kelsos.mbrc.content.library.tracks

import com.kelsos.mbrc.interfaces.data.Data
import com.kelsos.mbrc.utilities.RemoteUtils.sha1

interface Track : Data {
  var artist: String
  var title: String
  var src: String
  var trackno: Int
  var disc: Int
  var albumArtist: String
  var album: String
  var genre: String
  var year: String
  var id: Long
}

fun Track.key(): String = sha1("${albumArtist}_$album")
