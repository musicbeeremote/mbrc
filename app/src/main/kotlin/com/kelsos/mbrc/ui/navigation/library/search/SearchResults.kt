package com.kelsos.mbrc.ui.navigation.library.search

import com.kelsos.mbrc.data.library.Album
import com.kelsos.mbrc.data.library.Artist
import com.kelsos.mbrc.data.library.Genre
import com.kelsos.mbrc.data.library.Track
import com.raizlabs.android.dbflow.list.FlowCursorList

data class SearchResults(var genreList: FlowCursorList<Genre>,
                         var artistList: FlowCursorList<Artist>,
                         var albumList: FlowCursorList<Album>,
                         var trackList: FlowCursorList<Track>)
