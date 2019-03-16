package com.kelsos.mbrc.ui.connectionmanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class ConnectionManagerViewModel(
  private val repository: ConnectionRepository,
  dispatchers: AppCoroutineDispatchers
) : ViewModel() {

  private val job: Job = Job()
  private val scope = CoroutineScope(dispatchers.database + job)

  var settings: LiveData<List<ConnectionSettingsEntity>> = repository.getAll()

  fun startDiscovery() {
    scope.launch {
      val result = repository.discover()
      Timber.v(result.toString())
    }
  }

  fun setDefault(settings: ConnectionSettingsEntity) {
    repository.setDefault(settings)
  }

  fun save(settings: ConnectionSettingsEntity) {
    scope.launch {
      repository.save(settings)
    }
  }

  fun delete(settings: ConnectionSettingsEntity) {
    scope.launch {
      repository.delete(settings)
    }
  }

  override fun onCleared() {
    job.cancel()
    super.onCleared()
  }
}