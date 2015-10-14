package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.dto.Lyrics;
import com.kelsos.mbrc.services.api.TrackService;

import rx.Single;

public class TrackLyricsInteractorImpl implements TrackLyricsInteractor {

  @Inject private TrackService api;

  @Override
  public Single<Lyrics> execute() {
    return api.getTrackLyrics().flatMap(lyrics -> {
      String trackLyrics = lyrics.getLyrics();

      trackLyrics = trackLyrics.replace("<p>", "\r\n")
          .replace("<br>", "\n")
          .replace("&lt;", "<")
          .replace("&gt;", ">")
          .replace("&quot;", "\"")
          .replace("&apos;", "'")
          .replace("&amp;", "&")
          .replace("<p>", "\r\n")
          .replace("<br>", "\n")
          .trim();

      lyrics.setLyrics(trackLyrics);
      return Single.just(lyrics);
    });
  }
}
