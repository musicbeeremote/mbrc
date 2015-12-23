package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.interactors.AlbumTrackInteractor;
import com.kelsos.mbrc.ui.views.AlbumTrackView;
import roboguice.util.Ln;
import rx.android.schedulers.AndroidSchedulers;

public class AlbumTracksPresenterImpl implements AlbumTracksPresenter {

  private AlbumTrackView view;
  @Inject private AlbumTrackInteractor albumTrackInteractor;

  @Override public void bind(AlbumTrackView view) {

    this.view = view;
  }

  @Override public void load(long albumId) {
    albumTrackInteractor.execute(albumId).observeOn(AndroidSchedulers.mainThread()).subscribe(albumTrackModel -> {
      view.updateTracks(albumTrackModel.getTracks());
      view.updateAlbum(albumTrackModel.getAlbum());
    }, Ln::v);
  }

  @Override public void play() {

  }
}
