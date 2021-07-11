package com.kelsos.mbrc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.networking.ClientConnectionUseCase
import com.kelsos.mbrc.networking.connections.ConnectionState
import com.kelsos.mbrc.networking.connections.ConnectionStatus
import com.kelsos.mbrc.networking.protocol.VolumeModifyUseCase
import com.kelsos.mbrc.platform.ServiceChecker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NavigationViewModel(
  connectionState: ConnectionState,
  private val volume: VolumeModifyUseCase,
  private val connectionUseCase: ClientConnectionUseCase,
  private val serviceChecker: ServiceChecker
) : ViewModel() {
  val connection: Flow<ConnectionStatus> = connectionState.connection
  fun startService() = serviceChecker.startServiceIfNotRunning()

  fun connect() = connectionUseCase.connect()

  fun incrementVolume() {
    viewModelScope.launch {
      volume.increment()
    }
  }

  fun descrementVolume() {
    viewModelScope.launch {
      volume.decrement()
    }
  }
}
