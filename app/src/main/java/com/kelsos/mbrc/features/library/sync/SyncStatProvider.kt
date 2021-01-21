package com.kelsos.mbrc.features.library.sync

import com.kelsos.mbrc.common.utilities.AppDispatchers
import com.kelsos.mbrc.features.library.repositories.AlbumRepository
import com.kelsos.mbrc.features.library.repositories.ArtistRepository
import com.kelsos.mbrc.features.library.repositories.GenreRepository
import com.kelsos.mbrc.features.library.repositories.TrackRepository
import com.kelsos.mbrc.features.playlists.repository.PlaylistRepository
import com.kelsos.mbrc.metrics.SyncedData
import com.kelsos.mbrc.metrics.empty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SyncStatProvider(
  val dispatchers: AppDispatchers,
  private val genreRepository: GenreRepository,
  private val artistRepository: ArtistRepository,
  private val albumRepository: AlbumRepository,
  private val trackRepository: TrackRepository,
  private val playlistRepository: PlaylistRepository,
) {
  private val _stats: MutableStateFlow<SyncedData> = MutableStateFlow(SyncedData.empty())
  private val job = Job()
  private val scope = CoroutineScope(dispatchers.database + job)

  val stats: StateFlow<SyncedData> get() = _stats

  init {
    update()
  }

  fun update() {
    scope.launch {
      _stats.tryEmit(
        SyncedData(
          genreRepository.count(),
          artistRepository.count(),
          albumRepository.count(),
          trackRepository.count(),
          playlistRepository.count()
        )
      )
    }
  }
}
