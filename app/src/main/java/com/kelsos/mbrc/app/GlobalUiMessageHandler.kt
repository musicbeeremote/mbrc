package com.kelsos.mbrc.app

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import com.kelsos.mbrc.R
import com.kelsos.mbrc.networking.client.UiMessage
import com.kelsos.mbrc.networking.client.UiMessageQueue

private const val PLUGIN_RELEASES_URL = "https://github.com/musicbeeremote/plugin/releases"

/**
 * Composable that consumes global UI messages (connection errors, etc.)
 * and displays them via snackbar.
 */
@Composable
fun GlobalUiMessageHandler(
  uiMessageQueue: UiMessageQueue,
  snackbarHostState: SnackbarHostState,
  onPluginUpdateRequired: (version: String) -> Unit = {}
) {
  val strings = uiMessageStrings()
  val uriHandler = LocalUriHandler.current

  LaunchedEffect(Unit) {
    uiMessageQueue.messages.collect { message ->
      when (message) {
        is UiMessage.PluginUpdateRequired -> onPluginUpdateRequired(message.minimumVersion)

        is UiMessage.PluginUpdateAvailable -> {
          val result = snackbarHostState.showSnackbar(
            message = strings.pluginUpdateAvailable,
            actionLabel = strings.learnMore,
            duration = SnackbarDuration.Long
          )
          if (result == SnackbarResult.ActionPerformed) {
            uriHandler.openUri(PLUGIN_RELEASES_URL)
          }
        }

        else -> message.toDisplayText(strings)?.let { text ->
          snackbarHostState.showSnackbar(message = text, duration = SnackbarDuration.Long)
        }
      }
    }
  }
}

/**
 * Maps a [UiMessage] to its display text, or null if it shouldn't be shown as a snackbar.
 */
private fun UiMessage.toDisplayText(strings: UiMessageStrings): String? = when (this) {
  is UiMessage.ConnectionError.ServerNotFound -> strings.serverNotFound

  is UiMessage.ConnectionError.ConnectionTimeout -> strings.connectionTimeout

  is UiMessage.ConnectionError.ConnectionRefused -> strings.connectionRefused

  is UiMessage.ConnectionError.NetworkUnavailable -> strings.networkUnavailable

  is UiMessage.ConnectionError.AuthenticationFailed -> strings.authenticationFailed

  is UiMessage.ConnectionError.UnsupportedProtocolVersion -> strings.unsupportedProtocol

  is UiMessage.ConnectionError.AllRetriesExhausted -> strings.allRetriesExhausted

  is UiMessage.ConnectionError.UnknownConnectionError -> strings.unknownError.format(message)

  is UiMessage.NotAllowed -> strings.notAllowed

  is UiMessage.PartyModeCommandUnavailable -> strings.partyMode

  // These are handled separately in the main handler
  is UiMessage.PluginUpdateAvailable, is UiMessage.PluginUpdateRequired -> null
}

/**
 * Holder for pre-resolved string resources for UI messages.
 */
@Stable
data class UiMessageStrings(
  val serverNotFound: String,
  val connectionTimeout: String,
  val connectionRefused: String,
  val networkUnavailable: String,
  val authenticationFailed: String,
  val unsupportedProtocol: String,
  val allRetriesExhausted: String,
  val unknownError: String,
  val notAllowed: String,
  val partyMode: String,
  val pluginUpdateAvailable: String,
  val learnMore: String
)

@Composable
private fun uiMessageStrings() = UiMessageStrings(
  serverNotFound = stringResource(R.string.connection_error_server_not_found),
  connectionTimeout = stringResource(R.string.connection_error_connection_timeout),
  connectionRefused = stringResource(R.string.connection_error_connection_refused),
  networkUnavailable = stringResource(R.string.connection_error_network_unavailable),
  authenticationFailed = stringResource(R.string.connection_error_authentication_failed),
  unsupportedProtocol = stringResource(R.string.connection_error_unsupported_protocol),
  allRetriesExhausted = stringResource(R.string.connection_error_all_retries_exhausted),
  unknownError = stringResource(R.string.connection_error_unknown),
  notAllowed = stringResource(R.string.connection_error_not_allowed),
  partyMode = stringResource(R.string.connection_error_party_mode),
  pluginUpdateAvailable = stringResource(R.string.plugin_update_available),
  learnMore = stringResource(R.string.action_learn_more)
)
