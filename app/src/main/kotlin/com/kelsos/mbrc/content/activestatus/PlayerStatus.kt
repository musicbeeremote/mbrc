package com.kelsos.mbrc.content.activestatus

import com.kelsos.mbrc.networking.protocol.Protocol
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlayerStatus(
  @Json(name = Protocol.PlayerMute)
  val mute: Boolean,
  @Json(name = Protocol.PlayerState)
  val playState: String,
  @Json(name = Protocol.PlayerRepeat)
  val repeat: String,
  @Json(name = Protocol.PlayerShuffle)
  val shuffle: String,
  @Json(name = Protocol.PlayerScrobble)
  val scrobbling: Boolean,
  @Json(name = Protocol.PlayerVolume)
  val volume: Int
)