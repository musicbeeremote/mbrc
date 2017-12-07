package com.kelsos.mbrc.ui.navigation.library.search

import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.SchedulerProvider
import io.reactivex.Single
import io.reactivex.functions.Function4
import timber.log.Timber
import javax.inject.Inject

class SearchResultsPresenterImpl
@Inject constructor(
    private val genreRepository: GenreRepository,
    private val artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository,
    private val trackRepository: TrackRepository,
    private val schedulerProvider: SchedulerProvider
) : BasePresenter<SearchResultsView>(),
    SearchResultsPresenter {
  override fun search(term: String) {

    addDisposable(Single.zip(genreRepository.search(term),
        artistRepository.search(term),
        albumRepository.search(term),
        trackRepository.search(term),
        Function4(::SearchResults))
        .subscribeOn(schedulerProvider.io())
        .observeOn(schedulerProvider.main())
        .subscribe({
          view().update(it)
        }) {
          Timber.v(it)
        })
  }
}
