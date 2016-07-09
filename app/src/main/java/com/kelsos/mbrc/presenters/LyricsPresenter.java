package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.model.LyricsModel;
import com.kelsos.mbrc.views.LyricsView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.kelsos.mbrc.constants.Const.LYRICS_NEWLINE;

public class LyricsPresenter implements BasePresenter<LyricsView> {
  private LyricsView view;
  @Inject
  private LyricsModel model;

  @Override
  public void attach(LyricsView view) {
    this.view = view;
  }

  @Override
  public void detach() {
    this.view = null;
  }

  @Override
  public boolean isAttached() {
    return this.view != null;
  }

  public void load() {
    if (!isAttached()) {
      return;
    }

    updateLyrics(model.getLyrics());
  }

  public void updateLyrics(String text) {
    if (!isAttached()) {
      return;
    }
    final List<String> lyrics = new ArrayList<>(Arrays.asList(text.split(LYRICS_NEWLINE)));
    this.view.updateLyrics(lyrics);
  }
}
