package com.kelsos.mbrc.ui.navigation.radio

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.DataSource
import androidx.paging.PagedList
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup.NOW
import com.kelsos.mbrc.content.nowplaying.queue.QueueApi
import com.kelsos.mbrc.content.radios.RadioRepository
import com.kelsos.mbrc.content.radios.RadioStationEntity
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.AppRxSchedulers
import com.kelsos.mbrc.utilities.paged
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

@RadioActivity.Presenter
class RadioPresenterImpl
@Inject
constructor(
  private val radioRepository: RadioRepository,
  private val queueApi: QueueApi,
  private val appRxSchedulers: AppRxSchedulers
) : BasePresenter<RadioView>(),
  RadioPresenter {

  private lateinit var radios: LiveData<PagedList<RadioStationEntity>>

  override fun load() {
    view().loading(true)

    disposables += radioRepository.getAll()
      .subscribeOn(appRxSchedulers.database)
      .observeOn(appRxSchedulers.main)
      .doAfterTerminate { view().loading(false) }
      .subscribe({
        onRadiosLoaded(it)
      }, {
        view().error(it)
        Timber.v(it, "Failed")
      })
  }

  private fun onRadiosLoaded(factory: DataSource.Factory<Int, RadioStationEntity>) {
    radios = factory.paged()
    radios.observe(this, Observer {
      if (it != null) {
        view().update(it)
      }
    })
  }

  override fun refresh() {
    disposables += radioRepository.getRemote()
      .subscribeOn(appRxSchedulers.network)
      .observeOn(appRxSchedulers.main)
      .doAfterTerminate { view().loading(false) }
      .subscribe({
        //todo: dasda
      }, {
        view().error(it)
      })
  }

  override fun play(path: String) {
    disposables += queueApi.queue(NOW, listOf(path))
      .subscribeOn(appRxSchedulers.disk)
      .observeOn(appRxSchedulers.main)
      .subscribe({
        view().radioPlaySuccessful()
      }, {
        view().radioPlayFailed(it)
      })
  }
}