package com.kelsos.mbrc.ui.navigation.library.search

import com.kelsos.mbrc.content.library.albums.Album
import com.kelsos.mbrc.content.library.artists.Artist
import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.content.library.tracks.Track
import com.raizlabs.android.dbflow.list.FlowCursorList

data class SearchResults(var genreList: FlowCursorList<Genre>,
                         var artistList: FlowCursorList<Artist>,
                         var albumList: FlowCursorList<Album>,
                         var trackList: FlowCursorList<Track>)
