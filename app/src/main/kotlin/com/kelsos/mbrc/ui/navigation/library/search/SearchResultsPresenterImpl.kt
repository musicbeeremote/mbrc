package com.kelsos.mbrc.ui.navigation.library.search

import android.arch.paging.DataSource
import com.kelsos.mbrc.content.library.albums.AlbumEntity
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.content.library.genres.GenreEntity
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.content.library.tracks.TrackEntity
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.AppRxSchedulers
import com.kelsos.mbrc.utilities.paged
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class SearchResultsPresenterImpl
@Inject
constructor(
  private val genreRepository: GenreRepository,
  private val artistRepository: ArtistRepository,
  private val albumRepository: AlbumRepository,
  private val trackRepository: TrackRepository,
  private val appRxSchedulers: AppRxSchedulers
) : BasePresenter<SearchResultsView>(),
  SearchResultsPresenter {
  override fun search(term: String) {

    disposables +=
      Singles.zip(genreRepository.search(term),
        artistRepository.search(term),
        albumRepository.search(term),
        trackRepository.search(term),
        { genreList: DataSource.Factory<Int, GenreEntity>,
          artistList: DataSource.Factory<Int, ArtistEntity>,
          albumList: DataSource.Factory<Int, AlbumEntity>,
          trackList: DataSource.Factory<Int, TrackEntity> ->

          val genres = genreList.paged()
          val artists = artistList.paged()
          val albums = albumList.paged()
          val tracks = trackList.paged()

          SearchResults(genres, artists, albums, tracks)
        })
        .subscribeOn(appRxSchedulers.database)
        .observeOn(appRxSchedulers.main)
        .subscribe({
          view().update(it)
        }) {
          Timber.v(it)
        }
  }
}