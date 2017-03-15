package com.kelsos.mbrc.ui.navigation.library.search

import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.repository.AlbumRepository
import com.kelsos.mbrc.repository.ArtistRepository
import com.kelsos.mbrc.repository.GenreRepository
import com.kelsos.mbrc.repository.TrackRepository
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.functions.Function4
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class SearchResultsPresenterImpl
@Inject constructor(
    private val genreRepository: GenreRepository,
    private val artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository,
    private val trackRepository: TrackRepository,
    @Named("io") private val ioScheduler: Scheduler,
    @Named("main") private val mainScheduler: Scheduler
) : BasePresenter<SearchResultsView>(),
    SearchResultsPresenter {
  override fun search(term: String) {

    addDisposable(Single.zip(genreRepository.search(term),
        artistRepository.search(term),
        albumRepository.search(term),
        trackRepository.search(term),
        Function4(::SearchResults))
        .subscribeOn(ioScheduler)
        .observeOn(mainScheduler)
        .subscribe({
          view?.update(it)
        }) {
          Timber.v(it)
        })
  }
}
