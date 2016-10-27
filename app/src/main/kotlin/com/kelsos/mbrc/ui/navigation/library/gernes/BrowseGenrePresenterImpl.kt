package com.kelsos.mbrc.ui.navigation.library.gernes

import com.kelsos.mbrc.data.library.Genre
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.repository.GenreRepository
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Scheduler
import rx.Single
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class BrowseGenrePresenterImpl
@Inject constructor(private val repository: GenreRepository,
                    @Named("io") private val ioScheduler: Scheduler,
                    @Named("main") private val mainScheduler: Scheduler) :
    BasePresenter<BrowseGenreView>(),
    BrowseGenrePresenter {

  override fun load() {
    addSubcription(repository.getAllCursor().compose { schedule(it) }.subscribe({
      view?.update(it)
    }, {
      Timber.v(it, "Error while loading the data from the database")
      view?.failure(it)
    }))
  }


  override fun reload() {
    addSubcription(repository.getAndSaveRemote().compose  { schedule(it) }.subscribe({
      view?.update(it)
    }, {
      Timber.v(it, "Error while loading the data from the database")
      view?.failure(it)
    }))
  }

  private fun schedule(it: Single<FlowCursorList<Genre>>) = it.observeOn(mainScheduler)
      .subscribeOn(ioScheduler)

}

