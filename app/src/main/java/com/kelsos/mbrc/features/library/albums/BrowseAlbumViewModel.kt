package com.kelsos.mbrc.features.library.albums

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
class BrowseAlbumViewModel(
  private val repository: AlbumRepository,
  private val librarySyncUseCase: LibrarySyncUseCase,
  queueHandler: QueueHandler,
  settingsManager: SettingsManager,
  connectionStateFlow: ConnectionStateFlow,
  private val searchModel: LibrarySearchModel
) : BaseAlbumViewModel(queueHandler, settingsManager, connectionStateFlow) {
  override val albums: Flow<PagingData<Album>> =
    searchModel.term
      .flatMapLatest { keyword ->
        if (keyword.isEmpty()) {
          repository.getAll()
        } else {
          repository.search(keyword)
        }
      }.cachedIn(viewModelScope)

  val showSync = searchModel.term.map { it.isEmpty() }

  fun sync() {
    viewModelScope.launch {
      if (!checkConnection()) {
        emit(AlbumUiMessage.NetworkUnavailable)
        return@launch
      }
      librarySyncUseCase.sync()
    }
  }
}
