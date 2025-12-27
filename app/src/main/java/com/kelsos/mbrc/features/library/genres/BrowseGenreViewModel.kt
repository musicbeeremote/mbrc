package com.kelsos.mbrc.features.library.genres

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.common.state.ConnectionStateFlow
import com.kelsos.mbrc.features.library.LibrarySearchModel
import com.kelsos.mbrc.features.library.LibrarySyncUseCase
import com.kelsos.mbrc.features.queue.QueueHandler
import com.kelsos.mbrc.features.settings.SettingsManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class BrowseGenreViewModel(
  private val repository: GenreRepository,
  private val librarySyncUseCase: LibrarySyncUseCase,
  queueHandler: QueueHandler,
  private val searchModel: LibrarySearchModel,
  settingsManager: SettingsManager,
  connectionStateFlow: ConnectionStateFlow
) : BaseGenreViewModel(queueHandler, settingsManager, connectionStateFlow) {
  override val genres: Flow<PagingData<Genre>> =
    searchModel.term
      .flatMapLatest { term ->
        if (term.isEmpty()) {
          repository.getAll()
        } else {
          repository.search(term)
        }
      }.cachedIn(viewModelScope)

  val showSync = searchModel.term.map { it.isEmpty() }

  fun sync() {
    viewModelScope.launch {
      if (!checkConnection()) {
        emit(GenreUiMessage.NetworkUnavailable)
        return@launch
      }
      librarySyncUseCase.sync()
    }
  }
}
