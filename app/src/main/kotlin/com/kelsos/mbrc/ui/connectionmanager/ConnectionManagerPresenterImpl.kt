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
import com.kelsos.mbrc.utilities.SchedulerProvider
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import timber.log.Timber
import javax.inject.Inject

class ConnectionManagerPresenterImpl
@Inject
constructor(
    private val repository: ConnectionRepository,
    private val schedulerProvider: SchedulerProvider,
    private val bus: RxBus
) : BasePresenter<ConnectionManagerView>(), ConnectionManagerPresenter {

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
    val all = Observable.defer { Observable.just(repository.all) }
    val defaultId = Observable.defer { Observable.just(repository.defaultId) }

    addDisposable(Observable.zip<Long,
        List<ConnectionSettings>,
        ConnectionModel>(defaultId, all, BiFunction(::ConnectionModel))
        .subscribe({
          view().updateModel(it)
        }, {
          this.onLoadError(it)
        }))
  }

  override fun setDefault(settings: ConnectionSettings) {
    checkIfAttached()
    repository.default = settings
    bus.post(DefaultSettingsChangedEvent())
    view().dataUpdated()
  }

  override fun save(settings: ConnectionSettings) {
    checkIfAttached()

    if (settings.id > 0) {
      repository.update(settings)
    } else {
      repository.save(settings)
    }

    if (settings.id == repository.defaultId) {
      bus.post(DefaultSettingsChangedEvent())
    }

    view().dataUpdated()
  }

  override fun delete(settings: ConnectionSettings) {
    checkIfAttached()
    repository.delete(settings)
    if (settings.id == repository.defaultId) {
      bus.post(DefaultSettingsChangedEvent())
    }

    view().dataUpdated()
  }

  private fun onLoadError(throwable: Throwable) {
    checkIfAttached()
    Timber.v(throwable, "Failure")
  }
}
