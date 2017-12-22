package com.kelsos.mbrc.ui.connectionmanager

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import com.kelsos.mbrc.events.ConnectionSettingsChanged
import com.kelsos.mbrc.events.DiscoveryStopped
import com.kelsos.mbrc.events.NotifyUser
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.StartServiceDiscoveryEvent
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import com.kelsos.mbrc.preferences.DefaultSettingsChangedEvent
import com.kelsos.mbrc.utilities.SchedulerProvider
import timber.log.Timber
import javax.inject.Inject

class ConnectionManagerPresenterImpl
@Inject
constructor(
    private val repository: ConnectionRepository,
    private val schedulerProvider: SchedulerProvider,
    private val bus: RxBus
) : BasePresenter<ConnectionManagerView>(), ConnectionManagerPresenter {

  private lateinit var settings: LiveData<List<ConnectionSettingsEntity>>

  override fun attach(view: ConnectionManagerView) {
    super.attach(view)
    addDisposable(bus.observe(ConnectionSettingsChanged::class)
        .subscribeOn(schedulerProvider.io())
        .observeOn(schedulerProvider.main())
        .subscribe({ view().onConnectionSettingsChange(it) }))

    addDisposable(bus.observe(DiscoveryStopped::class)
        .subscribeOn(schedulerProvider.io())
        .observeOn(schedulerProvider.main())
        .subscribe({ view().onDiscoveryStopped(it) }))

    addDisposable(bus.observe(NotifyUser::class)
        .subscribeOn(schedulerProvider.io())
        .observeOn(schedulerProvider.main())
        .subscribe({ view().onUserNotification(it) }))
  }

  override fun startDiscovery() {
    bus.post(StartServiceDiscoveryEvent())
  }

  override fun load() {
    checkIfAttached()
    addDisposable(repository.getModel()
        .subscribeOn(schedulerProvider.io())
        .observeOn(schedulerProvider.main())
        .subscribe({

          settings = it.settings
          view().updateDefault(it.defaultId)

          settings.observe(this, Observer {
            it?.let { data ->
              view().updateData(data)
            }
          })

        }, {
          this.onLoadError(it)
        }))
  }

  override fun setDefault(settings: ConnectionSettingsEntity) {
    checkIfAttached()
    repository.default = settings
    bus.post(DefaultSettingsChangedEvent())
  }

  override fun save(settings: ConnectionSettingsEntity) {
    checkIfAttached()
    repository.save(settings)

    if (settings.id == repository.defaultId) {
      bus.post(DefaultSettingsChangedEvent())
    }

  }

  override fun delete(settings: ConnectionSettingsEntity) {
    checkIfAttached()

    repository.delete(settings)

    if (settings.id == repository.defaultId) {
      bus.post(DefaultSettingsChangedEvent())
    }
  }

  private fun onLoadError(throwable: Throwable) {
    checkIfAttached()
    Timber.v(throwable, "Failure")
  }
}
