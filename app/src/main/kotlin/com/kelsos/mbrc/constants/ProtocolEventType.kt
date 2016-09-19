package com.kelsos.mbrc.constants

object ProtocolEventType {
  val InitiateProtocolRequest = "InitiateProtocolRequest"
  val ReduceVolume = "ReduceVolume"
  val HandshakeComplete = "HandshakeComplete"
  val InformClientNotAllowed = "InformClientNotAllowed"
  val InformClientPluginOutOfDate = "InformClientPluginOutOfDate"
  val UserAction = "UserAction"
  val PluginVersionCheck = "PluginVersionCheck"
}
