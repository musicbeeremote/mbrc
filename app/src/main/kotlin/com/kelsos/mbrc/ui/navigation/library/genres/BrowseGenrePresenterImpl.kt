package com.kelsos.mbrc.ui.navigation.library.genres

import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.events.LibraryRefreshCompleteEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.SchedulerProvider
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class BrowseGenrePresenterImpl
@Inject constructor(
    private val bus: RxBus,
    private val repository: GenreRepository,
    private val schedulerProvider: SchedulerProvider
) :
    BasePresenter<BrowseGenreView>(),
    BrowseGenrePresenter {


  override fun attach(view: BrowseGenreView) {
    super.attach(view)
    bus.register(this, LibraryRefreshCompleteEvent::class.java, { load() })
  }

  override fun detach() {
    super.detach()
    bus.unregister(this)
  }

  override fun load() {
    view().showLoading()
    addDisposable(repository.getAllCursor().compose { schedule(it) }.subscribe({
      view().update(it)
      view().hideLoading()
    }, {
      Timber.v(it, "Error while loading the data from the database")
      view().failure(it)
      view().hideLoading()
    }))
  }


  override fun reload() {
    view().showLoading()
    addDisposable(repository.getAndSaveRemote().compose { schedule(it) }.subscribe({
      view().update(it)
      view().hideLoading()
    }, {
      Timber.v(it, "Error while loading the data from the database")
      view().failure(it)
      view().hideLoading()
    }))
  }

  private fun schedule(it: Single<List<Genre>>) = it.observeOn(schedulerProvider.main())
      .subscribeOn(schedulerProvider.io())

}

