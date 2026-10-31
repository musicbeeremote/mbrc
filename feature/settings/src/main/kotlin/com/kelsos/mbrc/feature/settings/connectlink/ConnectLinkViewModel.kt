package com.kelsos.mbrc.feature.settings.connectlink

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.core.common.data.ConnectionSettings
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.common.state.ConnectionStatus
import com.kelsos.mbrc.core.networking.LocalNetworkAccess
import com.kelsos.mbrc.core.networking.client.UiMessage
import com.kelsos.mbrc.core.networking.client.UiMessageQueue
import com.kelsos.mbrc.core.networking.connectlink.ConnectLink
import com.kelsos.mbrc.core.networking.connectlink.ConnectLinkError
import com.kelsos.mbrc.core.networking.connectlink.ConnectLinkParser
import com.kelsos.mbrc.core.networking.connectlink.ConnectLinkResult
import com.kelsos.mbrc.core.networking.connectlink.ReachableHostFinder
import com.kelsos.mbrc.feature.settings.domain.ConnectionRepository
import com.kelsos.mbrc.feature.settings.domain.DefaultConnectionSwitcher
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/** Why the last attempt to connect from a link stopped short. */
enum class ConnectProblem {
  /** No address in the link answered as a plugin. */
  Unreachable,

  /** Android 17 local network access is off, so no address could even be tried. */
  LocalNetworkDenied,

  /** A plugin answered, but the host could not be stored. */
  SaveFailed
}

@Stable
sealed interface ConnectLinkState {
  data object Hidden : ConnectLinkState

  /**
   * Waiting for the user's tap: a link never connects on its own.
   *
   * @property saved the connection already stored at one of the link's addresses, or null when this
   * computer is new to the app.
   * @property problem what went wrong on the previous attempt, or null before the first one.
   */
  data class Confirm(
    val link: ConnectLink,
    val saved: ConnectionSettings?,
    val problem: ConnectProblem?
  ) : ConnectLinkState

  data object Connecting : ConnectLinkState

  /**
   * The link names the default host and the app is connected or already connecting to it, so there
   * is nothing to do. Offering Connect here would only restart a connection that is under way.
   */
  data class AlreadyConnected(val name: String) : ConnectLinkState

  data class Rejected(val error: ConnectLinkError) : ConnectLinkState
}

/**
 * Takes a connect link from the plugin's QR code (musicbeeremote/mbrc#66) from confirmation to a
 * saved default host and a fresh connection. It uses the V4 handshake only; matching hosts by
 * [ConnectLink.serverId] and redeeming [ConnectLink.pairingCode] wait for protocol V6
 * (musicbeeremote/mbrc#406).
 *
 * A computer counts as saved by the rule in [ConnectionRepository.findSaved]. The link is kept in
 * [savedState] until it is used or dismissed, so a confirm dialog the system killed the process
 * under comes back.
 */
class ConnectLinkViewModel(
  private val savedState: SavedStateHandle,
  private val repository: ConnectionRepository,
  private val connectionState: ConnectionStateFlow,
  private val hostFinder: ReachableHostFinder,
  private val defaultConnectionSwitcher: DefaultConnectionSwitcher,
  private val localNetworkAccess: LocalNetworkAccess,
  private val uiMessageQueue: UiMessageQueue
) : ViewModel() {

  private val _state = MutableStateFlow<ConnectLinkState>(ConnectLinkState.Hidden)
  val state: StateFlow<ConnectLinkState> = _state.asStateFlow()

  private var working: Job? = null

  init {
    savedState.get<String>(KEY_LINK)?.let(::show)
  }

  fun open(link: String) {
    savedState[KEY_LINK] = link
    show(link)
  }

  fun connect() {
    val confirm = _state.value as? ConnectLinkState.Confirm ?: return
    if (!localNetworkAccess.isPermitted()) {
      _state.value = confirm.copy(problem = ConnectProblem.LocalNetworkDenied)
      return
    }
    _state.value = ConnectLinkState.Connecting
    working = viewModelScope.launch {
      val link = confirm.link
      val address = hostFinder.firstReachable(link.hosts, link.port)
      if (address == null) {
        _state.value = confirm.copy(problem = ConnectProblem.Unreachable)
        return@launch
      }
      switchTo(confirm, address)
    }
  }

  /** Clears a local network refusal once access has been granted, so the dialog offers Connect. */
  fun refreshLocalNetworkAccess() {
    val current = _state.value as? ConnectLinkState.Confirm ?: return
    if (current.problem == ConnectProblem.LocalNetworkDenied && localNetworkAccess.isPermitted()) {
      _state.value = current.copy(problem = null)
    }
  }

  fun dismiss() {
    working?.cancel()
    close()
  }

  private fun show(link: String) {
    working?.cancel()
    working = viewModelScope.launch {
      _state.value = when (val result = ConnectLinkParser.parse(link)) {
        is ConnectLinkResult.Valid -> stateFor(result.link)
        is ConnectLinkResult.Invalid -> ConnectLinkState.Rejected(result.error)
      }
    }
  }

  private suspend fun stateFor(link: ConnectLink): ConnectLinkState {
    val saved = repository.findSaved(link.hosts, link.port, link.name)
    if (saved != null && saved.isDefault && connectionState.connection.value.isUnderWay()) {
      return ConnectLinkState.AlreadyConnected(saved.name.ifBlank { saved.address })
    }
    return ConnectLinkState.Confirm(link, saved, problem = null)
  }

  private suspend fun switchTo(confirm: ConnectLinkState.Confirm, address: String) {
    val link = confirm.link
    val name = confirm.saved?.name ?: link.name ?: address
    try {
      defaultConnectionSwitcher.switchTo(address, link.port, name, confirm.saved?.id) { saved ->
        close()
        uiMessageQueue.messages.emit(UiMessage.ConnectingTo(saved.name))
      }
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Timber.e(e, "Failed to save %s:%d from a connect link", address, link.port)
      _state.value = confirm.copy(problem = ConnectProblem.SaveFailed)
    }
  }

  private fun ConnectionStatus.isUnderWay(): Boolean = this is ConnectionStatus.Connected ||
    this is ConnectionStatus.Connecting ||
    this == ConnectionStatus.Authenticating

  private fun close() {
    savedState.remove<String>(KEY_LINK)
    _state.value = ConnectLinkState.Hidden
  }

  private companion object {
    const val KEY_LINK = "connect_link"
  }
}
