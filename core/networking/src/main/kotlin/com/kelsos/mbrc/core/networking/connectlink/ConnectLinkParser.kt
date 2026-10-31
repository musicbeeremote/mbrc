package com.kelsos.mbrc.core.networking.connectlink

import com.kelsos.mbrc.core.networking.connectlink.ConnectLinkResult.Invalid
import com.kelsos.mbrc.core.networking.connectlink.ConnectLinkResult.Valid
import java.net.URI
import java.net.URLDecoder

/**
 * Reads schema v1 of the plugin's connect link (musicbeeremote/mbrc#66).
 *
 * The payload is read from the fragment, or from the query string when there is no fragment: the
 * fallback page's "Open in app" button is an `intent://` link whose own syntax takes the `#`.
 * Unknown keys are ignored, so a plugin can add an optional key without bumping `v`.
 *
 * A link is an input any web page or NFC tag can fire, so anything unexpected is refused with a
 * [ConnectLinkError] rather than guessed at.
 */
object ConnectLinkParser {
  const val SCHEMA_VERSION = 1
  const val MAX_HOSTS = 8
  const val MAX_NAME_LENGTH = 64

  private const val SCHEME = "https"
  private const val HOST = "mbrc.kelsos.net"
  private const val PATH = "/connect"
  private const val MAX_PORT = 65535
  private const val MAX_OCTET = 255
  private const val IPV4_OCTETS = 4

  /**
   * The accepted second octets for each first octet: `10/8`, `172.16/12`, `192.168/16`, and
   * `100.64/10` for Tailscale-style overlays.
   */
  @Suppress("MagicNumber")
  private val PRIVATE_RANGES: Map<Int, IntRange> = mapOf(
    10 to 0..MAX_OCTET,
    172 to 16..31,
    192 to 168..168,
    100 to 64..127
  )

  private val SERVER_ID =
    Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
  private val PAIRING_CODE = Regex("^[0-9]{6}$")
  private val OCTET = Regex("^(0|[1-9][0-9]{0,2})$")

  fun parse(link: String): ConnectLinkResult {
    val uri = runCatching { URI(link) }.getOrNull()
      ?: return Invalid(unreadableLinkError(link))
    if (!uri.isConnectPage()) {
      return Invalid(ConnectLinkError.NotAConnectLink)
    }
    val payload = uri.rawFragment?.takeIf { it.isNotEmpty() } ?: uri.rawQuery
    val keys = payload?.let(::decodePairs) ?: return Invalid(ConnectLinkError.Malformed)
    return read(keys)
  }

  /** A link `URI` cannot read is still ours, just damaged, when it starts with the page address. */
  private fun unreadableLinkError(link: String): ConnectLinkError =
    if (link.startsWith("$SCHEME://$HOST$PATH", ignoreCase = true)) {
      ConnectLinkError.Malformed
    } else {
      ConnectLinkError.NotAConnectLink
    }

  private fun URI.isConnectPage(): Boolean = SCHEME.equals(scheme, ignoreCase = true) &&
    HOST.equals(host, ignoreCase = true) &&
    path == PATH

  @Suppress("ReturnCount")
  private fun read(keys: Map<String, String>): ConnectLinkResult {
    val version = keys["v"]?.toIntOrNull() ?: return Invalid(ConnectLinkError.Malformed)
    if (version > SCHEMA_VERSION) {
      return Invalid(ConnectLinkError.NewerVersion)
    }
    if (version < SCHEMA_VERSION) {
      return Invalid(ConnectLinkError.Malformed)
    }

    val candidates = keys["h"]?.split(',')?.map(::parseIpv4)
    if (candidates == null || candidates.size > MAX_HOSTS || candidates.any { it == null }) {
      return Invalid(ConnectLinkError.Malformed)
    }
    val port = keys["p"]?.toIntOrNull()?.takeIf { it in 1..MAX_PORT }
      ?: return Invalid(ConnectLinkError.Malformed)
    val serverId = keys["id"]
    val pairingCode = keys["c"]
    val name = keys["n"]?.takeIf { it.isNotBlank() }
    val optionalsWellFormed = (serverId == null || SERVER_ID.matches(serverId)) &&
      (pairingCode == null || PAIRING_CODE.matches(pairingCode)) &&
      (name == null || name.length <= MAX_NAME_LENGTH)
    if (!optionalsWellFormed) {
      return Invalid(ConnectLinkError.Malformed)
    }

    val hosts = candidates.filterNotNull()
      .filter(::isPrivate)
      .map { it.joinToString(".") }
      .distinct()
    if (hosts.isEmpty()) {
      return Invalid(ConnectLinkError.NoLocalAddress)
    }

    return Valid(
      ConnectLink(
        hosts = hosts,
        port = port,
        name = name,
        serverId = serverId,
        pairingCode = pairingCode
      )
    )
  }

  /** The `key=value` pairs of [payload], percent-decoded, or null when any of them is broken. */
  private fun decodePairs(payload: String): Map<String, String>? {
    val keys = mutableMapOf<String, String>()
    for (pair in payload.split('&').filter { it.isNotEmpty() }) {
      val separator = pair.indexOf('=')
      if (separator <= 0) {
        return null
      }
      val key = percentDecode(pair.substring(0, separator)) ?: return null
      val value = percentDecode(pair.substring(separator + 1)) ?: return null
      keys[key] = value
    }
    return keys
  }

  /** Decodes `%XX` escapes only: a `+` is a literal plus here, not a form-encoded space. */
  private fun percentDecode(raw: String): String? = runCatching {
    URLDecoder.decode(raw.replace("+", "%2B"), Charsets.UTF_8.name())
  }.getOrNull()

  /** Strict dotted-quad parsing, so nothing in the link can trigger a DNS lookup. */
  private fun parseIpv4(address: String): List<Int>? {
    val octets = address.split('.')
    if (octets.size != IPV4_OCTETS || !octets.all(OCTET::matches)) {
      return null
    }
    return octets.map { it.toInt() }.takeIf { values -> values.all { it <= MAX_OCTET } }
  }

  private fun isPrivate(octets: List<Int>): Boolean {
    val (first, second) = octets
    return PRIVATE_RANGES[first]?.contains(second) == true
  }
}
