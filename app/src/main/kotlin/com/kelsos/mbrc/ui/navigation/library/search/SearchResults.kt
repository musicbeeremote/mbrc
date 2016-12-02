package com.kelsos.mbrc.ui.navigation.library.search

import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.domain.Artist
import com.kelsos.mbrc.domain.Genre
import com.kelsos.mbrc.domain.Track
import com.raizlabs.android.dbflow.list.FlowCursorList

data class SearchResults(var genreList: FlowCursorList<Genre>,
                         var artistList: FlowCursorList<Artist>,
                         var albumList: FlowCursorList<Album>,
                         var trackList: FlowCursorList<Track>)
