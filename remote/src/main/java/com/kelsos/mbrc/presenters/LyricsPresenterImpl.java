package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.events.ui.LyricsChangedEvent;
import com.kelsos.mbrc.interactors.TrackLyricsInteractor;
import com.kelsos.mbrc.ui.views.LyricsView;
import com.kelsos.mbrc.utilities.RxBus;
import roboguice.util.Ln;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LyricsPresenterImpl implements LyricsPresenter {

  private LyricsView view;
  @Inject private RxBus bus;
  @Inject private TrackLyricsInteractor lyricsInteractor;

  @Override public void bind(LyricsView view) {
    this.view = view;
    bus.register(this, LyricsChangedEvent.class, this::updateLyricsData);
  }

  @Override public void onPause() {

  }

  @Override public void onResume() {
    loadLyrics();
  }

  public void updateLyricsData(LyricsChangedEvent update) {
    view.updateLyrics(update.getLyrics());
  }

  private void loadLyrics() {
    lyricsInteractor.execute(false)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(view::updateLyrics, Ln::v);
  }
}
