package com.kelsos.mbrc.feature.library.tracks

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.kelsos.mbrc.core.common.settings.LibrarySettings
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.data.library.track.Track
import com.kelsos.mbrc.core.queue.Queue
import com.kelsos.mbrc.feature.library.BaseLibraryViewModel
import com.kelsos.mbrc.feature.library.queue.QueueHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

abstract class BaseTrackViewModel(
  private val queueHandler: QueueHandler,
  librarySettings: LibrarySettings,
  connectionStateFlow: ConnectionStateFlow
) : BaseLibraryViewModel<TrackUiMessage>(librarySettings, connectionStateFlow) {
  abstract val tracks: Flow<PagingData<Track>>

  fun queue(action: Queue, track: Track) {
    viewModelScope.launch {
      if (!checkConnection()) {
        emit(TrackUiMessage.NetworkUnavailable)
        return@launch
      }

      val queueAction = getQueueAction(action)
      val result = queueHandler.queueTrack(track = track, type = queueAction)

      val message =
        if (result.isSuccess) {
          TrackUiMessage.QueueSuccess(result.getOrNull() ?: 0)
        } else {
          TrackUiMessage.QueueFailed
        }

      emit(message)
    }
  }
}
