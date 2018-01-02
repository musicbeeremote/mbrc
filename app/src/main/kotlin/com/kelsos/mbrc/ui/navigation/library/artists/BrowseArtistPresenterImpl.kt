package com.kelsos.mbrc.ui.navigation.library.artists

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.events.LibraryRefreshCompleteEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.preferences.SettingsManager
import com.kelsos.mbrc.ui.navigation.library.ArtistTabRefreshEvent
import com.kelsos.mbrc.utilities.SchedulerProvider
import timber.log.Timber
import javax.inject.Inject

class BrowseArtistPresenterImpl
@Inject
constructor(
    private val bus: RxBus,
    private val repository: ArtistRepository,
    private val settingsManager: SettingsManager,
    private val schedulerProvider: SchedulerProvider
) : BasePresenter<BrowseArtistView>(),
    BrowseArtistPresenter {

  private lateinit var artists: LiveData<List<ArtistEntity>>

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
    val artistObservable = settingsManager.shouldDisplayOnlyAlbumArtists().flatMap {
      if (it) {
        return@flatMap repository.getAlbumArtistsOnly()
      } else {
        return@flatMap repository.getAll()
      }
    }
    addDisposable(artistObservable
        .observeOn(schedulerProvider.main())
        .subscribeOn(schedulerProvider.io())
        .subscribe({
          onArtistsLoaded(it)
          view().hideLoading()
        }, {
          Timber.v(it, "Error while loading the data from the database")
          view().hideLoading()
        }))

  }

  private fun onArtistsLoaded(it: LiveData<List<ArtistEntity>>) {
    if (::artists.isInitialized) {
      artists.removeObservers(this)
    }

    artists = it
    artists.observe(this, Observer {
      if (it != null) {
        view().update(it)
      }
    })
  }

  override fun reload() {
    val artistObservable = settingsManager.shouldDisplayOnlyAlbumArtists().flatMap {
      if (it) {
        return@flatMap repository.getAllRemoteAndShowAlbumArtist()
      } else {
        return@flatMap repository.getAndSaveRemote()
      }
    }
    addDisposable(artistObservable
        .observeOn(schedulerProvider.main())
        .subscribeOn(schedulerProvider.io())
        .subscribe({
          onArtistsLoaded(it)
          view().hideLoading()
        }, {
          Timber.v(it, "Error retrieving the data")
          view().hideLoading()
        }))
  }

}
