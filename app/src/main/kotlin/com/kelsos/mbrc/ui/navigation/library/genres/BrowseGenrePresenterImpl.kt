package com.kelsos.mbrc.ui.navigation.library.genres

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.paging.DataSource
import android.arch.paging.PagedList
import com.kelsos.mbrc.content.library.genres.GenreEntity
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.events.LibraryRefreshCompleteEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.SchedulerProvider
import com.kelsos.mbrc.utilities.paged
import timber.log.Timber
import javax.inject.Inject

class BrowseGenrePresenterImpl
@Inject
constructor(
    private val bus: RxBus,
    private val repository: GenreRepository,
    private val schedulerProvider: SchedulerProvider
) : BasePresenter<BrowseGenreView>(),
    BrowseGenrePresenter {

  private lateinit var genres: LiveData<PagedList<GenreEntity>>

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
    addDisposable(repository.getAll()
        .observeOn(schedulerProvider.main())
        .subscribeOn(schedulerProvider.io())
        .subscribe({
          onGenresLoaded(it)
          view().hideLoading()
        }, {
          Timber.v(it, "Error while loading the data from the database")
          view().failure(it)
          view().hideLoading()
        }))
  }

  private fun onGenresLoaded(it: DataSource.Factory<Int, GenreEntity>) {
    genres = it.paged()
    genres.observe(this, Observer {
      if (it != null) {
        view().update(it)
      }
    })
  }

  override fun reload() {
    view().showLoading()
    addDisposable(repository.getAndSaveRemote()
        .observeOn(schedulerProvider.main())
        .subscribeOn(schedulerProvider.io())
        .subscribe({
          onGenresLoaded(it)
          view().hideLoading()
        }, {
          Timber.v(it, "Error while loading the data from the database")
          view().failure(it)
          view().hideLoading()
        }))
  }

}

