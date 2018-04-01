package com.kelsos.mbrc.ui.connectionmanager

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.ConnectionSettingsEntity
import com.kelsos.mbrc.networking.discovery.ServiceDiscoveryUseCase
import com.kelsos.mbrc.utilities.SchedulerProvider
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class ConnectionManagerPresenterImpl
@Inject
constructor(
  private val repository: ConnectionRepository,
  private val serviceDiscoveryUseCase: ServiceDiscoveryUseCase,
  private val schedulerProvider: SchedulerProvider
) : BasePresenter<ConnectionManagerView>(), ConnectionManagerPresenter {

  private lateinit var settings: LiveData<List<ConnectionSettingsEntity>>

  override fun attach(view: ConnectionManagerView) {
    super.attach(view)
//    disposables += bus.observe(ConnectionSettingsChanged::class)
//      .subscribeOn(schedulerProvider.io())
//      .observeOn(schedulerProvider.main())
//      .subscribe({ view().onConnectionSettingsChange(it) })

//    disposables += bus.observe(DiscoveryStopped::class)
//      .subscribeOn(schedulerProvider.io())
//      .observeOn(schedulerProvider.main())
//      .subscribe({ view().onDiscoveryStopped(it) })

//    disposables += bus.observe(NotifyUser::class)
//      .subscribeOn(schedulerProvider.io())
//      .observeOn(schedulerProvider.main())
//      .subscribe({ view().onUserNotification(it) })
  }

  override fun startDiscovery() {
    serviceDiscoveryUseCase.discover {
      view().onDiscoveryStopped(it)
    }
  }

  override fun load() {
    checkIfAttached()
    disposables += repository.getModel()
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
      })
  }

  override fun setDefault(settings: ConnectionSettingsEntity) {
    checkIfAttached()
    repository.default = settings
    //bus.post(DefaultSettingsChangedEvent())
  }

  override fun save(settings: ConnectionSettingsEntity) {
    checkIfAttached()
    repository.save(settings)

    if (settings.id == repository.defaultId) {
      //bus.post(DefaultSettingsChangedEvent())
    }
  }

  override fun delete(settings: ConnectionSettingsEntity) {
    checkIfAttached()

    repository.delete(settings)

    if (settings.id == repository.defaultId) {
      //bus.post(DefaultSettingsChangedEvent())
    }
  }

  private fun onLoadError(throwable: Throwable) {
    checkIfAttached()
    Timber.v(throwable, "Failure")
  }
}