package com.kelsos.mbrc.features.settings

import com.kelsos.mbrc.features.queue.Queue
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface UpdateChecking {
  suspend fun isPluginUpdateCheckEnabled(): Boolean

  suspend fun getLastUpdated(required: Boolean = false): Instant

  suspend fun setLastUpdated(
    lastChecked: Instant,
    required: Boolean = false,
  )

  suspend fun setPluginUpdateCheck(enabled: Boolean)
}

interface SettingsManager {
  val state: Flow<SettingsState>
  val updates: UpdateChecking

  fun onlyAlbumArtists(): Flow<Boolean>

  suspend fun setShouldDisplayOnlyAlbumArtist(onlyAlbumArtist: Boolean)

  suspend fun shouldShowChangeLog(): Boolean

  suspend fun getCallAction(): CallAction

  suspend fun setDebugLogging(enabled: Boolean)

  suspend fun setCallAction(callAction: CallAction)

  suspend fun setLibraryAction(queue: Queue)
}
