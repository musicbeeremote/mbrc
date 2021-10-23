package com.kelsos.mbrc.features.settings

import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface SettingsManager {
  fun onlyAlbumArtists(): Flow<Boolean>
  suspend fun setShouldDisplayOnlyAlbumArtist(onlyAlbumArtist: Boolean)
  suspend fun shouldShowChangeLog(): Boolean
  suspend fun isPluginUpdateCheckEnabled(): Boolean
  suspend fun getCallAction(): CallAction
  suspend fun getLastUpdated(required: Boolean = false): Instant
  suspend fun setLastUpdated(lastChecked: Instant, required: Boolean = false)
}
