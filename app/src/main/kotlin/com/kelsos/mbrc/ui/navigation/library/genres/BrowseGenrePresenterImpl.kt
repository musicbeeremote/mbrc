package com.kelsos.mbrc.ui.navigation.library.genres

import androidx.paging.DataSource
import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.content.library.genres.GenreRepository
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

class BrowseGenrePresenterImpl
@Inject
constructor(
  private val bus: RxBus,
  private val repository: GenreRepository,
  private val librarySyncInteractor: LibrarySyncInteractor,
  private val queue: QueueHandler,
  private val searchModel: LibrarySearchModel
) : BasePresenter<BrowseGenreView>(), BrowseGenrePresenter {

  override fun attach(view: BrowseGenreView) {
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

  private fun updateUi(term: String) {
    scope.launch {
      view().showLoading()
      view().search(term)
      try {
        val data = getData(term)
        val liveData = data.paged()
        liveData.observe(
          this@BrowseGenrePresenterImpl,
          {
            if (it != null) {
              view().update(it)
            }
          }
        )
      } catch (e: Exception) {
        Timber.v(e, "Error while loading the data from the database")
      }
      view().hideLoading()
    }
  }

  private suspend fun getData(term: String): DataSource.Factory<Int, Genre> {
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

  override fun queue(action: String, genre: Genre) {
    scope.launch {
      val genreName = genre.genre
      val (success, tracks) = queue.queueGenre(action, genreName)
      view().queue(success, tracks)
    }
  }
}
