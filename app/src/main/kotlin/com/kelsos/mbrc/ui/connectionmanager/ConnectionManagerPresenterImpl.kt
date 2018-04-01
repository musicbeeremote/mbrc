package com.kelsos.mbrc.ui.connectionmanager

import androidx.lifecycle.LiveData
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class ConnectionManagerPresenterImpl
@Inject
constructor(
  private val repository: ConnectionRepository
) : BasePresenter<ConnectionManagerView>(), ConnectionManagerPresenter {

  private lateinit var settings: LiveData<List<ConnectionSettingsEntity>>

  override fun load() {
    checkIfAttached()
    scope.launch {
      try {
        val model = repository.getModel()
        settings = model.settings
        view().updateDefault(model.defaultId)

        settings.observe(this@ConnectionManagerPresenterImpl) {
          it?.let { data ->
            view().updateData(data)
          }
        }
      } catch (e: Exception) {
        Timber.v(e, "Failure")
      }
    }
  }

  override fun setDefault(settings: ConnectionSettingsEntity) {
    checkIfAttached()
    scope.launch {
      repository.setDefault(settings)
      // bus.post(DefaultSettingsChangedEvent())
      load()
    }
  }

  override fun save(settings: ConnectionSettingsEntity) {
    checkIfAttached()

    scope.launch {
      try {
        repository.save(settings)

        if (settings.id == repository.defaultId) {
          // bus.post(DefaultSettingsChangedEvent())
        }

        load()
      } catch (e: Exception) {
        Timber.v(e)
      }
    }
  }

  override fun delete(settings: ConnectionSettingsEntity) {
    checkIfAttached()

    scope.launch {
      repository.delete(settings)

      if (settings.id == repository.defaultId) {
        // bus.post(DefaultSettingsChangedEvent())
      }
    }
  }

  override fun startDiscovery() {
    TODO("Not yet implemented")
  }
}
