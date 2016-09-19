package com.kelsos.mbrc.data

import com.fasterxml.jackson.databind.JsonNode

class MusicTrack {
  var title: String? = null
  var artist: String? = null
    private set
  var position: Int = 0
    private set

  constructor(node: JsonNode) {
    this.artist = node.path("Artist").textValue()
    this.title = node.path("Title").textValue()
    this.position = node.path("Position").intValue()
  }

  constructor(artist: String, title: String) {
    this.artist = artist
    this.title = title
    position = 0
  }

  override fun equals(o: Any?): Boolean {
    var rValue = false
    if (o is MusicTrack) {
      if (o.title == this.title && o.artist == this.artist) {
        rValue = true
      }
    }
    return rValue
  }

  override fun hashCode(): Int {
    return title!!.hashCode() + artist!!.hashCode()
  }
}
