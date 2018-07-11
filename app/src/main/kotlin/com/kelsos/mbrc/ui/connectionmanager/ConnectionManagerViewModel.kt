package com.kelsos.mbrc.ui.connectionmanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import com.kelsos.mbrc.networking.discovery.ServiceDiscoveryUseCase
import com.kelsos.mbrc.utilities.AppRxSchedulers
import timber.log.Timber

class ConnectionManagerViewModel(
  private val repository: ConnectionRepository,
  private val serviceDiscoveryUseCase: ServiceDiscoveryUseCase,
  private val appRxSchedulers: AppRxSchedulers
) : ViewModel() {

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

    repository.save(settings)

    if (settings.id == repository.defaultId) {
      //bus.post(DefaultSettingsChangedEvent())
    }
  }

  fun delete(settings: ConnectionSettingsEntity) {


    repository.delete(settings)

    if (settings.id == repository.defaultId) {
      //bus.post(DefaultSettingsChangedEvent())
    }
  }

  private fun onLoadError(throwable: Throwable) {

    Timber.v(throwable, "Failure")
  }
}