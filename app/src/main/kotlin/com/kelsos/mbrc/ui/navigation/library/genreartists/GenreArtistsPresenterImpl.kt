package com.kelsos.mbrc.ui.navigation.library.genreartists

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.content.library.artists.Artist
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.helper.QueueHandler
import com.kelsos.mbrc.mvp.BasePresenter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class GenreArtistsPresenterImpl
@Inject
constructor(
  private val repository: ArtistRepository,
  private val queue: QueueHandler
) : BasePresenter<GenreArtistsView>(), GenreArtistsPresenter {

  private lateinit var artists: Flow<PagingData<Artist>>

  override fun load(genre: String) {
    scope.launch {
      try {
        val data = repository.getArtistByGenre(genre)
        artists = data.cachedIn(scope)
        artists.collectLatest { view().update(it) }
      } catch (e: Exception) {
        Timber.v(e)
      }
    }
  }

  override fun queue(@LibraryPopup.Action action: String, entry: Artist) {
    scope.launch {
      val artist = entry.artist
      val (success, tracks) = queue.queueArtist(action, artist)
      view().queue(success, tracks)
    }
  }
}
