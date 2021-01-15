package com.kelsos.mbrc.preferences

import kotlinx.coroutines.flow.StateFlow
import java.util.Date

interface SettingsManager {
  fun onlyAlbumArtists(): StateFlow<Boolean>
  fun setShouldDisplayOnlyAlbumArtist(onlyAlbumArtist: Boolean)
  fun shouldShowChangeLog(): Boolean
  fun isPluginUpdateCheckEnabled(): Boolean
  fun getCallAction(): CallAction
  fun getLastUpdated(): Date
  fun setLastUpdated(lastChecked: Date)
}
