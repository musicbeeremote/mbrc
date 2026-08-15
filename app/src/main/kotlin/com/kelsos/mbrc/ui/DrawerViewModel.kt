package com.kelsos.mbrc.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.common.state.ConnectionStatePublisher
import com.kelsos.mbrc.core.common.state.ConnectionStatus
import com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers
import com.kelsos.mbrc.core.networking.ClientConnectionUseCase
import com.kelsos.mbrc.core.networking.LocalNetworkAccess
import com.kelsos.mbrc.feature.settings.domain.ConnectionRepository
import com.kelsos.mbrc.service.ServiceChecker
import com.kelsos.mbrc.service.ServiceLifecycleManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DrawerViewModel(
  private val connectionStateFlow: ConnectionStateFlow,
  private val clientConnectionUseCase: ClientConnectionUseCase,
  private val connectionRepository: ConnectionRepository,
  private val dispatchers: AppCoroutineDispatchers,
  private val serviceChecker: ServiceChecker,
  private val serviceLifecycleManager: ServiceLifecycleManager,
  private val localNetworkAccess: LocalNetworkAccess,
  private val connectionStatePublisher: ConnectionStatePublisher
) : ViewModel() {

  val connectionStatus: StateFlow<ConnectionStatus> = connectionStateFlow.connection

  private val _connectionName = MutableStateFlow<String?>(null)
  val connectionName: StateFlow<String?> = _connectionName.asStateFlow()

  init {
    observeConnectionStatus()
  }

  private fun observeConnectionStatus() {
    viewModelScope.launch {
      connectionStatus.collect { status ->
        if (status is ConnectionStatus.Connected) {
          loadConnectionName()
        } else {
          _connectionName.value = null
        }
      }
    }
  }

  private suspend fun loadConnectionName() {
    val default = withContext(dispatchers.database) {
      connectionRepository.getDefault()
    } ?: return
    _connectionName.value = default.name.ifBlank { "${default.address}:${default.port}" }
  }

  fun isConnected(): Boolean = connectionStatus.value is ConnectionStatus.Connected

  private fun isConnectingOrConnected(): Boolean = when (connectionStatus.value) {
    is ConnectionStatus.Connected,
    is ConnectionStatus.Connecting,
    is ConnectionStatus.Authenticating -> true

    is ConnectionStatus.Offline,
    is ConnectionStatus.LocalNetworkDenied -> false
  }

  /**
   * Attempts to connect, unless local network access is denied: there is nothing to attempt in that
   * case, and showing a connection cycle would blame the network for a permission problem. Returns
   * false so the caller can ask the user for access instead.
   */
  fun toggleConnection(): Boolean {
    if (!isConnectingOrConnected() && !localNetworkAccess.isPermitted()) {
      connectionStatePublisher.updateConnection(ConnectionStatus.LocalNetworkDenied)
      return false
    }
    viewModelScope.launch {
      if (isConnectingOrConnected()) {
        // Notify that this is an intentional disconnect to prevent reconnection
        serviceLifecycleManager.onIntentionalDisconnect()
        clientConnectionUseCase.disconnect()
      } else {
        // Ensure service is running before connecting (same as BaseActivity)
        serviceChecker.startServiceIfNotRunning()
        clientConnectionUseCase.connect()
      }
    }
    return true
  }

  /**
   * Re-checks access after the user has been through the permission flow.
   *
   * Connects rather than just clearing the state: the user granted access in order to reach
   * MusicBee, and the service was never started while access was denied, so leaving them at "not
   * connected" would make them tap connect for no reason. Publishing Offline instead would also be
   * read as a connection loss and buy a 15s reconnection delay before the first attempt.
   */
  fun refreshLocalNetworkAccess() {
    if (!localNetworkAccess.isPermitted()) {
      return
    }
    if (connectionStatus.value !is ConnectionStatus.LocalNetworkDenied) {
      return
    }
    viewModelScope.launch {
      serviceChecker.startServiceIfNotRunning()
      clientConnectionUseCase.connect()
    }
  }
}
