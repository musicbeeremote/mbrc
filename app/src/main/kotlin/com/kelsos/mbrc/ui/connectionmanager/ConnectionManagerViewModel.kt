package com.kelsos.mbrc.ui.connectionmanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import com.kelsos.mbrc.networking.discovery.ServiceDiscoveryUseCase
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class ConnectionManagerViewModel(
  private val repository: ConnectionRepository,
  private val serviceDiscoveryUseCase: ServiceDiscoveryUseCase,
  private val dispatchers: AppCoroutineDispatchers
) : ViewModel() {

  private val viewmModelJob: Job = Job()
  private val databaseScope = CoroutineScope(dispatchers.database + viewmModelJob)

  var settings: LiveData<List<ConnectionSettingsEntity>> = repository.getAll()

  fun startDiscovery() {
    serviceDiscoveryUseCase.discover {
      //view().onDiscoveryStopped(it)
    }
  }

  fun setDefault(settings: ConnectionSettingsEntity) {

    repository.default = settings
    //bus.post(DefaultSettingsChangedEvent())
  }

  fun save(settings: ConnectionSettingsEntity) {
    databaseScope.launch {
      repository.save(settings)

      if (settings.id == repository.defaultId) {
        //bus.post(DefaultSettingsChangedEvent())
      }
    }

  }

  fun delete(settings: ConnectionSettingsEntity) {
    databaseScope.launch {

      repository.delete(settings)

      if (settings.id == repository.defaultId) {
        //bus.post(DefaultSettingsChangedEvent())
      }
    }

  }

  private fun onLoadError(throwable: Throwable) {

    Timber.v(throwable, "Failure")
  }

  override fun onCleared() {
    viewmModelJob.cancel()
    super.onCleared()
  }
}