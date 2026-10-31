package com.kelsos.mbrc.feature.library.tracks

import androidx.paging.PagingData
import com.kelsos.mbrc.core.common.settings.LibrarySettings
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.data.library.track.Track
import com.kelsos.mbrc.core.queue.Queue
import com.kelsos.mbrc.feature.library.BaseLibraryViewModel
import com.kelsos.mbrc.feature.library.queue.QueueHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

abstract class BaseTrackViewModel(
  private val queueHandler: QueueHandler,
  private val librarySettings: LibrarySettings,
  connectionStateFlow: ConnectionStateFlow
) : BaseLibraryViewModel<Nothing>(connectionStateFlow) {
  abstract val tracks: Flow<PagingData<Track>>

  fun queue(action: Queue, track: Track) {
    launchQueue { queueHandler.queueTrack(track = track, type = queueActionFor(action)) }
  }

  private suspend fun queueActionFor(action: Queue): Queue = if (action == Queue.Default) {
    Queue.fromTrackAction(librarySettings.libraryTrackDefaultActionFlow.first())
  } else {
    action
  }
}
