package com.kelsos.mbrc.ui.navigation.library.tracks

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.content.sync.LibrarySyncInteractor
import com.kelsos.mbrc.helper.QueueHandler
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.ui.navigation.library.LibrarySearchModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class BrowseTrackPresenterImpl
@Inject
constructor(
  private val repository: TrackRepository,
  private val librarySyncInteractor: LibrarySyncInteractor,
  private val queue: QueueHandler,
  private val searchModel: LibrarySearchModel
) : BasePresenter<BrowseTrackView>(), BrowseTrackPresenter {

  private lateinit var tracks: Flow<PagingData<Track>>

  override fun attach(view: BrowseTrackView) {
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
        onTrackLoad(getData(term))
      } catch (e: Exception) {
        Timber.v(e, "Error while loading the data from the database")
      }
      view().hideLoading()
    }
  }

  private fun onTrackLoad(data: Flow<PagingData<Track>>) {
    tracks = data.cachedIn(scope)
    scope.launch {
      data.collectLatest { view().update(it) }
    }
  }

  private suspend fun getData(term: String): Flow<PagingData<Track>> {
    return if (term.isEmpty()) {
      repository.getAll()
    } else {
      repository.search(term)
    }
  }

  override fun sync() {
    scope.launch {
      librarySyncInteractor.sync()
    }
  }

  override fun queue(track: Track, action: String?) {
    scope.launch {
      val (success, tracks) = if (action == null) {
        queue.queueTrack(track)
      } else {
        queue.queueTrack(track, action)
      }
      view().queue(success, tracks)
    }
  }
}
