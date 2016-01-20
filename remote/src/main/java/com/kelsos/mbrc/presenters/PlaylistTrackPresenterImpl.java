package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.Queue;
import com.kelsos.mbrc.domain.PlaylistTrack;
import com.kelsos.mbrc.interactors.QueueInteractor;
import com.kelsos.mbrc.interactors.playlists.PlaylistTrackInteractor;
import com.kelsos.mbrc.ui.views.PlaylistTrackView;
import roboguice.util.Ln;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PlaylistTrackPresenterImpl implements PlaylistTrackPresenter {
  private PlaylistTrackView view;

  @Inject private PlaylistTrackInteractor playlistTrackInteractor;
  @Inject private QueueInteractor queueInteractor;

  @Override public void bind(PlaylistTrackView view) {
    this.view = view;
  }

  @Override public void load(long playlistId) {
    playlistTrackInteractor.execute(playlistId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(playlistTracks -> {
          view.update(playlistTracks);
        }, throwable -> {
          view.showErrorWhileLoading();
          Ln.v(throwable, "");
        });
  }

  @Override public void queue(PlaylistTrack track, @Queue.Action String action) {
    queueInteractor.execute(action, track.getPath()).subscribe(aBoolean -> {

    }, throwable -> {
      Ln.v(throwable);
    });
  }
}
