package com.kelsos.mbrc.presenters;

import com.kelsos.mbrc.model.LyricsModel;
import com.kelsos.mbrc.views.LyricsView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;

import static com.kelsos.mbrc.constants.Const.LYRICS_NEWLINE;

public class LyricsPresenter extends BasePresenter<LyricsView> {
  @Inject LyricsModel model;

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
    getView().updateLyrics(lyrics);
  }
}
