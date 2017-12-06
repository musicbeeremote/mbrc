package com.kelsos.mbrc.ui.connectionmanager

import com.kelsos.mbrc.events.ConnectionSettingsChanged
import com.kelsos.mbrc.events.DiscoveryStopped
import com.kelsos.mbrc.events.NotifyUser
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.StartServiceDiscoveryEvent
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.ConnectionSettings
import com.kelsos.mbrc.preferences.DefaultSettingsChangedEvent
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class ConnectionManagerPresenterImpl
@Inject
constructor(
  private val repository: ConnectionRepository,
  private val bus: RxBus
) : BasePresenter<ConnectionManagerView>(), ConnectionManagerPresenter {

  override fun attach(view: ConnectionManagerView) {
    super.attach(view)
    bus.register(
      this, ConnectionSettingsChanged::class.java,
      {
        view().onConnectionSettingsChange(it)
      },
      true
    )

    bus.register(
      this, DiscoveryStopped::class.java,
      {
        view().onDiscoveryStopped(it)
      },
      true
    )

    bus.register(
      this, NotifyUser::class.java,
      {
        view().onUserNotification(it)
      },
      true
    )
  }

  override fun startDiscovery() {
    bus.post(StartServiceDiscoveryEvent())
  }

  override fun load() {
    checkIfAttached()
    scope.launch {
      try {
        val settings = repository.getAll()
        view?.updateModel(ConnectionModel(repository.defaultId, settings))
      } catch (e: Exception) {
        Timber.v(e, "Failure")
      }
    }
  }

  override fun setDefault(settings: ConnectionSettings) {
    checkIfAttached()
    scope.launch {
      repository.setDefault(settings)
      bus.post(DefaultSettingsChangedEvent())
      view?.dataUpdated()
    }
  }

  override fun save(settings: ConnectionSettings) {
    checkIfAttached()
    scope.launch {
      try {
        if (settings.id > 0) {
          repository.update(settings)
        } else {
          repository.save(settings)
        }

        if (settings.id == repository.defaultId) {
          bus.post(DefaultSettingsChangedEvent())
        }

        view?.dataUpdated()
      } catch (e: Exception) {
        Timber.v(e)
      }
    }
  }

  override fun delete(settings: ConnectionSettings) {
    scope.launch {
      checkIfAttached()
      repository.delete(settings)
      if (settings.id == repository.defaultId) {
        bus.post(DefaultSettingsChangedEvent())
      }

      view?.dataUpdated()
    }
  }
}
