package com.kelsos.mbrc.ui.navigation.library.artists

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import com.kelsos.mbrc.content.library.artists.Artist
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.content.sync.LibrarySyncInteractor
import com.kelsos.mbrc.events.LibraryRefreshCompleteEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.helper.QueueHandler
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.preferences.SettingsManager
import com.kelsos.mbrc.ui.navigation.library.ArtistTabRefreshEvent
import com.kelsos.mbrc.ui.navigation.library.LibrarySearchModel
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class BrowseArtistPresenterImpl
@Inject
constructor(
  private val bus: RxBus,
  private val repository: ArtistRepository,
  private val settingsManager: SettingsManager,
  private val librarySyncInteractor: LibrarySyncInteractor,
  private val queue: QueueHandler,
  private val searchModel: LibrarySearchModel
) : BasePresenter<BrowseArtistView>(), BrowseArtistPresenter {

  private lateinit var artists: LiveData<PagedList<Artist>>

  override fun attach(view: BrowseArtistView) {
    super.attach(view)
    scope.launch {
      searchModel.term.collect { term -> updateUi(term) }
    }
    bus.register(this, LibraryRefreshCompleteEvent::class.java) { load() }
    bus.register(this, ArtistTabRefreshEvent::class.java) { load() }
  }

  override fun detach() {
    super.detach()
    bus.unregister(this)
  }

  override fun load() {
    updateUi(searchModel.term.value)
  }

  private fun updateUi(term: String) {
    scope.launch {
      view().showLoading()
      view().search(term)
      try {
        onArtistsLoaded(getData(term))
      } catch (e: Exception) {
        Timber.v(e, "Error while loading the data from the database")
      }
      view().hideLoading()
    }
  }

  private fun onArtistsLoaded(factory: DataSource.Factory<Int, Artist>) {
    artists = factory.paged()
    artists.observe(
      this@BrowseArtistPresenterImpl,
      {
        if (it != null) {
          view().update(it)
        }
      }
    )
  }

  private suspend fun getData(term: String): DataSource.Factory<Int, Artist> {
    return if (term.isEmpty()) {
      val shouldDisplay = settingsManager.shouldDisplayOnlyAlbumArtists()
      if (shouldDisplay) {
        repository.getAlbumArtistsOnly()
      } else {
        repository.getAll()
      }
    } else {
      repository.search(term)
    }
  }

  override fun sync() {
    scope.launch {
      librarySyncInteractor.sync()
    }
  }

  override fun queue(action: String, entry: Artist) {
    scope.launch {
      val artist = entry.artist
      val (success, tracks) = queue.queueArtist(action, artist)
      view().queue(success, tracks)
    }
  }
}
