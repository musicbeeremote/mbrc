package com.kelsos.mbrc.ui.navigation.library.search

import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.library.artists.Artist
import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.content.library.tracks.Track

data class SearchResults(
    var genreList: List<Genre>,
    var artistList: List<Artist>,
    var albumList: List<Album>,
    var trackList: List<Track>
)
