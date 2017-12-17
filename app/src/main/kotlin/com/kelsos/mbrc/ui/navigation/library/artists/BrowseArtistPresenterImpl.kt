package com.kelsos.mbrc.ui.navigation.library.artists

import android.arch.lifecycle.Observer
import android.arch.paging.DataSource
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.events.LibraryRefreshCompleteEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.preferences.SettingsManager
import com.kelsos.mbrc.ui.navigation.library.ArtistTabRefreshEvent
import com.kelsos.mbrc.utilities.SchedulerProvider
import com.kelsos.mbrc.utilities.paged
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class BrowseArtistPresenterImpl
@Inject constructor(
    private val bus: RxBus,
    private val repository: ArtistRepository,
    private val settingsManager: SettingsManager,
    private val schedulerProvider: SchedulerProvider
) :
    BasePresenter<BrowseArtistView>(),
    BrowseArtistPresenter {

  override fun attach(view: BrowseArtistView) {
    super.attach(view)
    bus.register(this, LibraryRefreshCompleteEvent::class.java, { load() })
    bus.register(this, ArtistTabRefreshEvent::class.java, { load() })
  }

  override fun detach() {
    super.detach()
    bus.unregister(this)
  }

  override fun load() {
    view().showLoading()
    val artistObservable = settingsManager.shouldDisplayOnlyAlbumArtists().flatMap {
      if (it) {
        return@flatMap repository.getAlbumArtistsOnly()
      } else {
        return@flatMap repository.getAll()
      }
    }
    addDisposable(artistObservable.compose { schedule(it) }.subscribe({
      val liveData = it.paged()
      liveData.observe(this, Observer {
        if (it != null) {
          view().update(it)
        }
      })
      view().hideLoading()
    }, {
      Timber.v(it, "Error while loading the data from the database")
      view().hideLoading()
    }))

  }

  override fun reload() {
    view().showLoading()
    val artistObservable = settingsManager.shouldDisplayOnlyAlbumArtists().flatMap {
      if (it) {
        return@flatMap repository.getAllRemoteAndShowAlbumArtist()
      } else {
        return@flatMap repository.getAndSaveRemote()
      }
    }
    addDisposable(artistObservable.compose { schedule(it) }.subscribe({
      val liveData = it.paged()
      liveData.observe(this, Observer {
        if (it != null) {
          view().update(it)
        }
      })
      view().hideLoading()
    }, {
      Timber.v(it, "Error retrieving the data")
      view().hideLoading()
    }))
  }

  private fun schedule(it: Single<DataSource.Factory<Int, ArtistEntity>>) = it.observeOn(schedulerProvider.main())
      .subscribeOn(schedulerProvider.io())

}
