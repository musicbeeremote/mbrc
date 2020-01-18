package com.kelsos.mbrc.ui.connectionmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.events.Event
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.ConnectionSettings
import com.kelsos.mbrc.networking.discovery.DiscoveryStop
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConnectionManagerViewModel(
  private val repository: ConnectionRepository,
  private val dispatchers: AppCoroutineDispatchers
) : ViewModel() {
  private val _discoveryStatus: MutableSharedFlow<Event<DiscoveryStop>> = MutableSharedFlow()
  val settings: Flow<PagingData<ConnectionSettings>> = repository.getAll().cachedIn(viewModelScope)
  val emitter: SharedFlow<Event<DiscoveryStop>> get() = _discoveryStatus

  fun startDiscovery() {
    viewModelScope.launch(dispatchers.network) {
      val result = repository.discover()
      withContext(dispatchers.main) {
        _discoveryStatus.tryEmit(Event(result))
      }
    }
  }

  fun setDefault(settings: ConnectionSettings) {
    viewModelScope.launch(dispatchers.database) {
      repository.setDefault(settings)
    }
  }

  fun save(settings: ConnectionSettings) {
    viewModelScope.launch(dispatchers.database) {
      repository.save(settings)
    }
  }

  fun delete(settings: ConnectionSettings) {
    viewModelScope.launch(dispatchers.database) {
      repository.delete(settings)
    }
  }
}
