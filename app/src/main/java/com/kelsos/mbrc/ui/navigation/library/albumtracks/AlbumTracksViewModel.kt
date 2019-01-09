package com.kelsos.mbrc.ui.navigation.library.albumtracks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.content.library.albums.AlbumInfo
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AlbumTracksViewModel(
  private val repository: TrackRepository
) : ViewModel() {
  private lateinit var tracks: Flow<PagingData<Track>>

  fun load(album: AlbumInfo) {
    viewModelScope.launch {
      val data = if (album.album.isEmpty()) {
        repository.getNonAlbumTracks(album.artist)
      } else {
        repository.getAlbumTracks(album.album, album.artist)
      }
      tracks = data.cachedIn(viewModelScope)
    }
  }
}
