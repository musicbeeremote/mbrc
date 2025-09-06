package com.kelsos.mbrc.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.common.state.ConnectionStateFlow
import com.kelsos.mbrc.common.state.ConnectionStatus
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.features.settings.ConnectionRepository
import com.kelsos.mbrc.networking.ClientConnectionUseCase
import com.kelsos.mbrc.platform.ServiceChecker
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
  private val serviceChecker: ServiceChecker
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
        clientConnectionUseCase.disconnect()
      } else {
        // Ensure service is running before connecting (same as BaseActivity)
        serviceChecker.startServiceIfNotRunning()
        clientConnectionUseCase.connect()
      }
    }
  }
}
