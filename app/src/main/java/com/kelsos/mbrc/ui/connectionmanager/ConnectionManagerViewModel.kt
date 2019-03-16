package com.kelsos.mbrc.ui.connectionmanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class ConnectionManagerViewModel(
  private val repository: ConnectionRepository,
  private val dispatchers: AppCoroutineDispatchers
) : ViewModel() {

  var settings: LiveData<List<ConnectionSettingsEntity>> = runBlocking { repository.getAll() }

  fun startDiscovery() {
    viewModelScope.launch(dispatchers.network) {
      val result = repository.discover()
      Timber.v(result.toString())
    }
  }

  fun setDefault(settings: ConnectionSettingsEntity) {
    viewModelScope.launch(dispatchers.network) {
      repository.setDefault(settings)
    }
  }

  fun save(settings: ConnectionSettingsEntity) {
    viewModelScope.launch(dispatchers.network) {
      repository.save(settings)
    }
  }

  fun delete(settings: ConnectionSettingsEntity) {
    viewModelScope.launch(dispatchers.network) {
      repository.delete(settings)
    }
  }
}
