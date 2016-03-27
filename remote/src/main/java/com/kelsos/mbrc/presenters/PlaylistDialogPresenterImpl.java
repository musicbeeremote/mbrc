package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.interactors.PlaylistInteractor;
import com.kelsos.mbrc.ui.views.PlaylistDialogView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PlaylistDialogPresenterImpl implements PlaylistDialogPresenter {
  private PlaylistDialogView view;

  @Inject private PlaylistInteractor playlistInteractor;

  @Override public void load() {
    playlistInteractor.getUserPlaylists()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(playlists -> {
          view.update(playlists);
        }, throwable -> {
          Timber.v(throwable, "Failed to load playlists");
        });
  }

  @Override public void bind(PlaylistDialogView view) {

    this.view = view;
  }
}
