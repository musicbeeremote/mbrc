package com.kelsos.mbrc.ui.navigation.library.artists

import com.kelsos.mbrc.data.library.Artist
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.LibraryRefreshCompleteEvent
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.repository.ArtistRepository
import com.kelsos.mbrc.ui.navigation.library.ArtistTabRefreshEvent
import com.kelsos.mbrc.utilities.SettingsManager
import com.raizlabs.android.dbflow.list.FlowCursorList
import io.reactivex.Scheduler
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class BrowseArtistPresenterImpl
@Inject constructor(private val bus: RxBus,
                    private val repository: ArtistRepository,
                    private val settingsManager: SettingsManager,
                    @Named("io") private val ioScheduler: Scheduler,
                    @Named("main") private val mainScheduler: Scheduler) :
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
    view?.showLoading()
    val artistObservable = settingsManager.shouldDisplayOnlyAlbumArtists().flatMap {
      if (it) {
        return@flatMap repository.getAlbumArtistsOnly()
      } else {
        return@flatMap repository.getAllCursor()
      }
    }
    addDisposable(artistObservable.compose { schedule(it) }.subscribe({
      view?.update(it)
      view?.hideLoading()
    }, {
      Timber.v(it, "Error while loading the data from the database")
      view?.hideLoading()
    }))

  }

  override fun reload() {
    view?.showLoading()
    val artistObservable = settingsManager.shouldDisplayOnlyAlbumArtists().flatMap {
      if (it) {
        return@flatMap repository.getAllRemoteAndShowAlbumArtist()
      } else {
        return@flatMap repository.getAndSaveRemote()
      }
    }
    addDisposable(artistObservable.compose { schedule(it) }.subscribe({
      view?.update(it)
      view?.hideLoading()
    }, {
      Timber.v(it, "Error retrieving the data")
      view?.hideLoading()
    }))
  }

  private fun schedule(it: Single<FlowCursorList<Artist>>) = it.observeOn(mainScheduler)
      .subscribeOn(ioScheduler)

}
