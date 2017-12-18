package com.kelsos.mbrc.ui.navigation.library.albums

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.content.sync.LibrarySyncInteractor
import com.kelsos.mbrc.events.LibraryRefreshCompleteEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.helper.QueueHandler
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.ui.navigation.library.LibrarySearchModel
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class BrowseAlbumPresenterImpl
@Inject
constructor(
  private val bus: RxBus,
  private val repository: AlbumRepository,
  private val librarySyncInteractor: LibrarySyncInteractor,
  private val queueHandler: QueueHandler,
  private val searchModel: LibrarySearchModel
) : BasePresenter<BrowseAlbumView>(), BrowseAlbumPresenter {

  private lateinit var albums: LiveData<PagedList<Album>>

  private fun updateUi(term: String) {
    scope.launch {
      view().showLoading()
      view().search(term)
      try {
        albums = getData(term).paged()
        albums.observe(
          this@BrowseAlbumPresenterImpl,
          {
            if (it != null) {
              view().update(it)
            }
          }
        )
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
    bus.register(this, LibraryRefreshCompleteEvent::class.java) { load() }
  }

  override fun detach() {
    super.detach()
    bus.unregister(this)
  }

  override fun load() {
    updateUi(searchModel.term.value)
  }

  override fun sync() {
    scope.launch {
      librarySyncInteractor.sync()
    }
  }

  override fun queue(action: String, entry: Album) {
    scope.launch {
      val (success, tracks) = queueHandler.queueAlbum(action, entry.album, entry.artist)
      view().queue(success, tracks)
    }
  }
}
