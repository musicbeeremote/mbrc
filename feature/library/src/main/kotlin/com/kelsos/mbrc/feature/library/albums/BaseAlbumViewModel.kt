package com.kelsos.mbrc.feature.library.albums

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.kelsos.mbrc.core.common.settings.AlbumSortPreference
import com.kelsos.mbrc.core.common.settings.AlbumViewMode
import com.kelsos.mbrc.core.common.settings.LibrarySettings
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.data.library.album.Album
import com.kelsos.mbrc.core.queue.Queue
import com.kelsos.mbrc.feature.library.BaseLibraryViewModel
import com.kelsos.mbrc.feature.library.queue.QueueHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

abstract class BaseAlbumViewModel(
  private val queueHandler: QueueHandler,
  private val librarySettings: LibrarySettings,
  connectionStateFlow: ConnectionStateFlow
) : BaseLibraryViewModel<AlbumUiMessage>(connectionStateFlow) {
  abstract val albums: Flow<PagingData<Album>>

  val sortPreference: Flow<AlbumSortPreference> =
    librarySettings.albumSortPreferenceFlow.distinctUntilChanged()
  val albumViewMode: Flow<AlbumViewMode> = librarySettings.albumViewModeFlow

  fun queue(queue: Queue, album: Album) {
    if (queue == Queue.Default) {
      launchDefault(AlbumUiMessage.OpenAlbumTracks(album))
      return
    }

    launchQueue { queueHandler.queueAlbum(queue, album.album, album.artist) }
  }

  fun updateSortPreference(preference: AlbumSortPreference) {
    viewModelScope.launch {
      librarySettings.setAlbumSortPreference(preference)
    }
  }

  fun toggleViewMode() {
    viewModelScope.launch {
      librarySettings.setAlbumViewMode(albumViewMode.first().next())
    }
  }
}
