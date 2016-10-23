package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.repository.TrackRepository
import rx.Observable
import javax.inject.Inject

class TrackLyricsInteractorImpl
@Inject constructor(private val repository: TrackRepository) :
    TrackLyricsInteractor {

  override fun execute(reload: Boolean): Observable<List<String>> {
    return repository.getTrackLyrics(reload)
  }
}
