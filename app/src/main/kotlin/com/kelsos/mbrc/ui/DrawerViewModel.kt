package com.kelsos.mbrc.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.common.state.ConnectionStatus
import com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers
import com.kelsos.mbrc.core.networking.ClientConnectionUseCase
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
  private val serviceLifecycleManager: ServiceLifecycleManager
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

  fun toggleConnection() {
    viewModelScope.launch {
      if (isConnected()) {
        // Notify that this is an intentional disconnect to prevent reconnection
        serviceLifecycleManager.onIntentionalDisconnect()
        clientConnectionUseCase.disconnect()
      } else {
        // Ensure service is running before connecting (same as BaseActivity)
        serviceChecker.startServiceIfNotRunning()
        clientConnectionUseCase.connect()
      }
    }
  }
}
