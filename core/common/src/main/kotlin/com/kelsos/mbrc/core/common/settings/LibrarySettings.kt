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

  // Library sorting preferences
  val genreSortPreferenceFlow: Flow<GenreSortPreference>
  val artistSortPreferenceFlow: Flow<ArtistSortPreference>
  val albumSortPreferenceFlow: Flow<AlbumSortPreference>
  val trackSortPreferenceFlow: Flow<TrackSortPreference>
  val genreArtistsSortPreferenceFlow: Flow<ArtistSortPreference>
  val artistAlbumsSortPreferenceFlow: Flow<AlbumSortPreference>

  suspend fun setGenreSortPreference(preference: GenreSortPreference)
  suspend fun setArtistSortPreference(preference: ArtistSortPreference)
  suspend fun setAlbumSortPreference(preference: AlbumSortPreference)
  suspend fun setTrackSortPreference(preference: TrackSortPreference)
  suspend fun setGenreArtistsSortPreference(preference: ArtistSortPreference)
  suspend fun setArtistAlbumsSortPreference(preference: AlbumSortPreference)

  // Album view mode
  val albumViewModeFlow: Flow<AlbumViewMode>
  suspend fun setAlbumViewMode(mode: AlbumViewMode)
}
