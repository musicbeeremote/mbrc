package com.kelsos.mbrc.ui.navigation.library.search

import android.arch.lifecycle.LiveData
import android.arch.paging.PagedList
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.content.library.genres.GenreEntity
import com.kelsos.mbrc.content.library.tracks.TrackEntity

data class SearchResults(
    var genreList: LiveData<List<GenreEntity>>,
    var artistList: LiveData<List<ArtistEntity>>,
    var albumList: LiveData<List<AlbumEntity>>,
    var trackList: LiveData<List<TrackEntity>>
) {
  fun empty(): Boolean {
    val noGenres = genreList.value?.isEmpty() ?: true
    val noArtists = artistList.value?.isEmpty() ?: true
    val noAlbums = albumList.value?.isEmpty() ?: true
    val noTracks = trackList.value?.isEmpty() ?: true
    return noGenres && noArtists && noAlbums && noTracks
  }
}
