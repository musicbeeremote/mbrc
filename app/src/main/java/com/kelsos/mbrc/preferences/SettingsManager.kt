package com.kelsos.mbrc.preferences

import kotlinx.coroutines.flow.StateFlow
import org.threeten.bp.Instant

interface SettingsManager {
  fun onlyAlbumArtists(): StateFlow<Boolean>
  fun setShouldDisplayOnlyAlbumArtist(onlyAlbumArtist: Boolean)
  fun shouldShowChangeLog(): Boolean
  fun isPluginUpdateCheckEnabled(): Boolean
  fun getCallAction(): CallAction
  fun getLastUpdated(required: Boolean = false): Instant
  fun setLastUpdated(lastChecked: Instant, required: Boolean = false)
}
