package com.kelsos.mbrc.core.networking.connectlink

/**
 * The connection details a plugin's QR code carries, parsed from a link such as
 * `https://mbrc.kelsos.net/connect#v=1&h=192.168.1.5,10.0.0.3&p=3000&n=LIVING-ROOM-PC`.
 *
 * [serverId] and [pairingCode] are validated but not acted on: both need protocol V6.
 *
 * @property hosts the private-range IPv4 candidates, most likely first.
 * @property name the host's display name, or null when the link does not carry one.
 */
data class ConnectLink(
  val hosts: List<String>,
  val port: Int,
  val name: String?,
  val serverId: String?,
  val pairingCode: String?
)

/** Why a link was refused. Every case is shown to the user, none of them crash. */
enum class ConnectLinkError {
  /** Not a `https://mbrc.kelsos.net/connect` link at all. */
  NotAConnectLink,

  /** Written by a newer plugin for a schema this version of the app does not know. */
  NewerVersion,

  /** A required key is missing, or a value is out of range or badly formed. */
  Malformed,

  /** Every address in the link was outside the private ranges. */
  NoLocalAddress
}

sealed interface ConnectLinkResult {
  data class Valid(val link: ConnectLink) : ConnectLinkResult

  data class Invalid(val error: ConnectLinkError) : ConnectLinkResult
}
