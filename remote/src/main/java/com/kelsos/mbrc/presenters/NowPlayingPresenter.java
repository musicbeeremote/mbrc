package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.domain.QueueTrack;
import com.kelsos.mbrc.interactors.NowPlayingListInteractor;
import com.kelsos.mbrc.interactors.nowplaying.NowPlayingActionInteractor;
import com.kelsos.mbrc.ui.views.NowPlayingView;
import roboguice.util.Ln;

public class NowPlayingPresenter {
  private NowPlayingView view;
  @Inject private NowPlayingListInteractor nowPlayingListInteractor;
  @Inject private NowPlayingActionInteractor nowPlayingActionInteractor;

  public void bind(NowPlayingView view) {
    this.view = view;
  }

  public void loadData() {
    nowPlayingListInteractor.execute()
        .subscribe(view::updateAdapter, Ln::v);
  }

  public void playTrack(final QueueTrack track) {
    nowPlayingActionInteractor.play(track.getPath())
        .subscribe(success -> {
          if (success) {
            view.updatePlayingTrack(track);
          }
        }, Ln::v);
  }

  public void removeItem(int position) {
    nowPlayingActionInteractor.remove(position).subscribe(aBoolean -> {

    });
  }

  public void moveItem(int from, int to) {
    nowPlayingActionInteractor.move(from, to).subscribe(aBoolean -> {

    });
  }
}
