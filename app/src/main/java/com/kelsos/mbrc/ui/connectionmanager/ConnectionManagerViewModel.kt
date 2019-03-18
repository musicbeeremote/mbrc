package com.kelsos.mbrc.ui.connectionmanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kelsos.mbrc.events.Event
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConnectionManagerViewModel(
  private val repository: ConnectionRepository,
  private val dispatchers: AppCoroutineDispatchers
) : ViewModel() {

  private val job: Job = Job()
  private val scope = CoroutineScope(dispatchers.database + job)
  private val _discoveryStatus: MutableLiveData<Event<Int>> = MutableLiveData()

  val settings: LiveData<List<ConnectionSettingsEntity>> = repository.getAll()
  val default: LiveData<ConnectionSettingsEntity?> = repository.defaultSettings()
  val discoveryStatus: LiveData<Event<Int>> get() = _discoveryStatus

  fun startDiscovery() {
    scope.launch {
      val result = repository.discover()
      withContext(dispatchers.main) {
        _discoveryStatus.value = Event(result)
      }
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