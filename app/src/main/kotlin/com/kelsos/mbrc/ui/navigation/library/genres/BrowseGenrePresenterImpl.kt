package com.kelsos.mbrc.ui.navigation.library.genres

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.content.library.genres.GenreRepository
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

class BrowseGenrePresenterImpl
@Inject
constructor(
  private val repository: GenreRepository,
  private val librarySyncInteractor: LibrarySyncInteractor,
  private val queue: QueueHandler,
  private val searchModel: LibrarySearchModel
) : BasePresenter<BrowseGenreView>(), BrowseGenrePresenter {

  private lateinit var genres: Flow<PagingData<Genre>>

  override fun attach(view: BrowseGenreView) {
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
        onGenresLoaded(getData(term))
      } catch (e: Exception) {
        Timber.v(e, "Error while loading the data from the database")
      }
      view().hideLoading()
    }
  }

  private suspend fun getData(term: String): Flow<PagingData<Genre>> {
    return if (term.isEmpty()) {
      repository.getAll()
    } else {
      repository.search(term)
    }
  }

  private fun onGenresLoaded(data: Flow<PagingData<Genre>>) {
    genres = data.cachedIn(scope)
    scope.launch {
      genres.collectLatest { view().update(it) }
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
