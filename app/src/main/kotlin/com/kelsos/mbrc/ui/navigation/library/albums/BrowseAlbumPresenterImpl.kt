package com.kelsos.mbrc.ui.navigation.library.albums

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.content.sync.LibrarySyncUseCase
import com.kelsos.mbrc.helper.QueueHandler
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.ui.navigation.library.LibrarySearchModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class BrowseAlbumPresenterImpl(
  private val repository: AlbumRepository,
  private val syncUseCase: LibrarySyncUseCase,
  private val queueHandler: QueueHandler,
  private val searchModel: LibrarySearchModel
) : BasePresenter<BrowseAlbumView>(), BrowseAlbumPresenter {

  private lateinit var albums: Flow<PagingData<Album>>

  private fun observeAlbums(data: Flow<PagingData<Album>>) {
    albums = data.cachedIn(scope)
    scope.launch {
      data.collectLatest { view().update(it) }
    }
  }

  private fun updateUi(term: String) {
    scope.launch {
      view().search(term)
      try {
        observeAlbums(getData(term))
      } catch (e: Exception) {
        Timber.v(e)
      }
      view().hideLoading()
    }
  }

  private suspend fun getData(term: String) =
    if (term.isNotEmpty()) repository.search(term) else repository.getAll()

  override fun attach(view: BrowseAlbumView) {
    super.attach(view)
    scope.launch {
      searchModel.term.collect { term -> updateUi(term) }
    }
  }

  override fun load() {
    updateUi(searchModel.term.value)
  }

  override fun sync() {
    scope.launch {
      syncUseCase.sync()
    }
  }

  override fun queue(action: String, entry: Album) {
    scope.launch {
      val (success, tracks) = queueHandler.queueAlbum(action, entry.album, entry.artist)
      view().queue(success, tracks)
    }
  }
}
