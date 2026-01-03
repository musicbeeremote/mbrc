package com.kelsos.mbrc.core.common.settings

import kotlinx.coroutines.flow.Flow

/**
 * Subset of settings relevant to the library feature.
 * Implemented by SettingsManager in the app module.
 */
interface LibrarySettings {
  val shouldDisplayOnlyArtists: Flow<Boolean>
  val libraryTrackDefaultActionFlow: Flow<TrackAction>
  suspend fun setShouldDisplayOnlyAlbumArtist(onlyAlbumArtist: Boolean)
}
