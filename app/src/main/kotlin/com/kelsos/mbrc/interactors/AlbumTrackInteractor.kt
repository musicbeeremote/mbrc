package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.domain.Album
import com.kelsos.mbrc.domain.AlbumTrackModel
import com.kelsos.mbrc.domain.Track
import com.kelsos.mbrc.extensions.io
import com.kelsos.mbrc.mappers.AlbumMapper
import com.kelsos.mbrc.mappers.TrackMapper
import com.kelsos.mbrc.repository.library.AlbumRepository
import com.kelsos.mbrc.repository.library.TrackRepository
import rx.Observable
import rx.lang.kotlin.toSingletonObservable
import javax.inject.Inject

class AlbumTrackInteractor
@Inject constructor(private val trackRepository: TrackRepository,
                    private val repository: AlbumRepository) {

  fun execute(id: Long): Observable<AlbumTrackModel> {
    val tracks = trackRepository.getTracksByAlbumId(id)
        .flatMap { TrackMapper.map(it).toSingletonObservable() }
    return Observable.zip<List<Track>, Album, AlbumTrackModel>(tracks,
        getAlbum(id),
        ::AlbumTrackModel).io()
  }

  private fun getAlbum(id: Long): Observable<Album> {
    return Observable.create<Album> {
      val albumView = repository.getAlbumViewById(id.toInt())
      if (albumView != null) {
        val album = AlbumMapper.map(albumView, repository.getAlbumYear(id))
        it.onNext(album)
      }
      it.onCompleted()
    }
  }
}
