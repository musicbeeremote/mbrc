package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.interactors.PlaylistActionInteractor;
import com.kelsos.mbrc.interactors.PlaylistInteractor;
import com.kelsos.mbrc.ui.views.PlaylistListView;
import roboguice.util.Ln;
import rx.android.schedulers.AndroidSchedulers;

public class PlaylistPresenterImpl implements PlaylistPresenter {
  private PlaylistListView view;

  @Inject private PlaylistInteractor playlistInteractor;
  @Inject private PlaylistActionInteractor actionInteractor;

  @Override public void bind(PlaylistListView view) {
    this.view = view;
  }

  @Override public void load() {
    playlistInteractor.execute()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(playlists -> view.update(playlists), Ln::v);
  }

  @Override public void play(String path) {
    actionInteractor.play(path).subscribe(aBoolean -> {

    }, throwable -> {

    });
  }
}
