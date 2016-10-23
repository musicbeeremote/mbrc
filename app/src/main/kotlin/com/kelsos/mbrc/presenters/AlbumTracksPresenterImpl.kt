package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.annotations.MetaDataType
import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.Track
import com.kelsos.mbrc.interactors.AlbumTrackInteractor
import com.kelsos.mbrc.interactors.QueueInteractor
import com.kelsos.mbrc.ui.views.AlbumTrackView
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber
import javax.inject.Inject

class AlbumTracksPresenterImpl
@Inject constructor(private val albumTrackInteractor: AlbumTrackInteractor,
                    private val interactor: QueueInteractor): AlbumTracksPresenter {

  private var view: AlbumTrackView? = null

  override fun bind(view: AlbumTrackView) {
    this.view = view
  }

  override fun load(albumId: Long) {
    albumTrackInteractor.execute(albumId).observeOn(AndroidSchedulers.mainThread()).subscribe({
      view?.updateTracks(it.tracks)
      view?.updateAlbum(it.album)
    }, { Timber.v(it) })
  }

  override fun play(albumId: Long) {
    interactor.execute(MetaDataType.ALBUM, Queue.NOW, albumId).subscribe({ aBoolean ->
      if (aBoolean!!) {
        view?.showPlaySuccess()
      } else {
        view?.showPlayFailed()
      }
    }) {
      view?.showPlayFailed()
      Timber.e(it, "Failed to play the album %d", albumId)
    }
  }

  override fun queue(entry: Track, @Queue.Action action: String) {
    val id = entry.id
    interactor.execute(MetaDataType.TRACK, action, id).subscribe({
      if (it!!) {
        view?.showTrackSuccess()
      } else {
        view?.showTrackFailed()
      }
    }) { t ->
      view?.showTrackFailed()
      Timber.e(t, "Failed to play the track %d", id)
    }
  }
}
