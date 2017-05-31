package com.kelsos.mbrc.ui.connection_manager

import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.connections.ConnectionSettings
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import timber.log.Timber
import javax.inject.Inject


class ConnectionManagerPresenterImpl
@Inject
constructor(private val repository: ConnectionRepository) : BasePresenter<ConnectionManagerView>(), ConnectionManagerPresenter {

  override fun load() {
    checkIfAttached()
    val all = Observable.defer { Observable.just(repository.all) }
    val defaultId = Observable.defer { Observable.just(repository.defaultId) }

    addDisposable(Observable.zip<Long,
        List<ConnectionSettings>,
        ConnectionModel>(defaultId, all, BiFunction(::ConnectionModel))
        .subscribe({
          view?.updateModel(it)
        }, {
          this.onLoadError(it)
        }))
  }

  override fun setDefault(settings: ConnectionSettings) {
    checkIfAttached()
    repository.default = settings
    view?.defaultChanged()
    view?.dataUpdated()
  }

  override fun save(settings: ConnectionSettings) {
    checkIfAttached()

    if (settings.id > 0) {
      repository.update(settings)
    } else {
      repository.save(settings)
    }

    if (settings.id == repository.defaultId) {
      view?.defaultChanged()
    }

    view?.dataUpdated()
  }

  override fun delete(settings: ConnectionSettings) {
    checkIfAttached()
    repository.delete(settings)
    if (settings.id == repository.defaultId) {
      view?.defaultChanged()
    }

    view?.dataUpdated()
  }

  private fun onLoadError(throwable: Throwable) {
    checkIfAttached()
    Timber.v(throwable, "Failure")
  }
}
