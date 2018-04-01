package com.kelsos.mbrc.ui.navigation.library.artists

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.content.library.artists.Artist
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.content.sync.LibrarySyncInteractor
import com.kelsos.mbrc.helper.QueueHandler
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.preferences.SettingsManager
import com.kelsos.mbrc.ui.navigation.library.LibrarySearchModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class BrowseArtistPresenterImpl
@Inject
constructor(
  private val repository: ArtistRepository,
  private val settingsManager: SettingsManager,
  private val librarySyncInteractor: LibrarySyncInteractor,
  private val queue: QueueHandler,
  private val searchModel: LibrarySearchModel
) : BasePresenter<BrowseArtistView>(),
  BrowseArtistPresenter {

  private lateinit var artists: Flow<PagingData<Artist>>

  override fun attach(view: BrowseArtistView) {
    super.attach(view)
    scope.launch {
      searchModel.term.collect { term -> updateUi(term) }
    }
  }

  override fun load() {
    updateUi(searchModel.term.value)
  }

  private fun updateUi(term: String) {
    scope.launch {
      view().search(term)
      try {
        onArtistsLoaded(getData(term))
      } catch (e: Exception) {
        Timber.v(e, "Error while loading the data from the database")
      }
      view().hideLoading()
    }
  }

  private fun onArtistsLoaded(artists: Flow<PagingData<Artist>>) {
    this.artists = artists.cachedIn(scope)
    scope.launch {
      artists.collectLatest { view().update(it) }
    }
  }

  private suspend fun getData(term: String): Flow<PagingData<Artist>> {
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
