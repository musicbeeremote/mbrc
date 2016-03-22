package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.MetaDataType;
import com.kelsos.mbrc.annotations.Queue;
import com.kelsos.mbrc.domain.Track;
import com.kelsos.mbrc.interactors.AlbumTrackInteractor;
import com.kelsos.mbrc.interactors.QueueInteractor;
import com.kelsos.mbrc.ui.views.AlbumTrackView;
import roboguice.util.Ln;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class AlbumTracksPresenterImpl implements AlbumTracksPresenter {

  private AlbumTrackView view;
  @Inject private AlbumTrackInteractor albumTrackInteractor;
  @Inject private QueueInteractor interactor;

  @Override public void bind(AlbumTrackView view) {
    this.view = view;
  }

  @Override public void load(long albumId) {
    albumTrackInteractor.execute(albumId).observeOn(AndroidSchedulers.mainThread()).subscribe(albumTrackModel -> {
      view.updateTracks(albumTrackModel.getTracks());
      view.updateAlbum(albumTrackModel.getAlbum());
    }, Ln::v);
  }

  @Override public void play(long albumId) {
    interactor.execute(MetaDataType.ALBUM, Queue.NOW, (int) albumId).subscribe(aBoolean -> {
      if (aBoolean) {
        view.showPlaySuccess();
      } else {
        view.showPlayFailed();
      }
    }, t -> {
      view.showPlayFailed();
      Timber.e(t, "Failed to play the album %d", albumId);
    });
  }

  @Override public void queue(Track entry, @Queue.Action String action) {
    final long id = entry.getId();
    interactor.execute(MetaDataType.TRACK, action, (int) id).subscribe(aBoolean -> {
      if (aBoolean) {
        view.showTrackSuccess();
      } else {
        view.showTrackFailed();
      }
    }, t -> {
      view.showTrackFailed();
      Timber.e(t, "Failed to play the track %d", id);
    });
  }
}
