package com.kelsos.mbrc.ui.navigation.library.tracks

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.paging.DataSource
import android.arch.paging.PagedList
import com.kelsos.mbrc.content.library.tracks.TrackEntity
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.SchedulerProvider
import com.kelsos.mbrc.utilities.paged
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class BrowseTrackPresenterImpl
@Inject
constructor(
  private val repository: TrackRepository,
  private val schedulerProvider: SchedulerProvider
) : BasePresenter<BrowseTrackView>(),
  BrowseTrackPresenter {

  private lateinit var tracks: LiveData<PagedList<TrackEntity>>
  private lateinit var indexes: LiveData<List<String>>

  override fun attach(view: BrowseTrackView) {
    super.attach(view)
    // listen for library refresh somehow
  }


  override fun load() {
    disposables += repository.allTracks()
      .observeOn(schedulerProvider.main())
      .subscribeOn(schedulerProvider.io())
      .doFinally { view().hideLoading() }
      .subscribe({
        onTrackLoad(it.factory)
        onIndexesLoad(it.indexes)
      }, {
        view().failure(it)
        Timber.e(it, "Error while loading the data from the database")
      })
  }

  private fun onIndexesLoad(indexes: LiveData<List<String>>) {
    if (this::indexes.isInitialized) {
      this.indexes.removeObservers(this)
    }
    this.indexes = indexes
    this.indexes.observe(this, Observer {
      if (it != null) {
        view().updateIndexes(it)
      }
    })
  }

  private fun onTrackLoad(it: DataSource.Factory<Int, TrackEntity>) {
    if (::tracks.isInitialized) {
      tracks.removeObservers(this)
    }

    tracks = it.paged()
    tracks.observe(this, Observer {
      if (it != null) {
        view().update(it)
      }
    })
  }

  override fun reload() {
    disposables += repository.getAndSaveRemote()
      .observeOn(schedulerProvider.main())
      .subscribeOn(schedulerProvider.io())
      .doFinally {
        view().hideLoading()
      }
      .subscribe({
        onTrackLoad(it)
      }, {
        view().failure(it)
        Timber.e(it, "Error while loading the data from the database")
      })
  }
}