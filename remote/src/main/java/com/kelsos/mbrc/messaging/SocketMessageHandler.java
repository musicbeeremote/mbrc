package com.kelsos.mbrc.messaging;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.constants.SocketNotification;
import com.kelsos.mbrc.dto.WebSocketMessage;
import com.kelsos.mbrc.events.ui.CoverChangedEvent;
import com.kelsos.mbrc.events.ui.LyricsChangedEvent;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.RepeatChange;
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent;
import com.kelsos.mbrc.events.ui.VolumeChangeEvent;
import com.kelsos.mbrc.interactors.PlayerStateInteractor;
import com.kelsos.mbrc.interactors.RepeatInteractor;
import com.kelsos.mbrc.interactors.TrackCoverInteractor;
import com.kelsos.mbrc.interactors.TrackInfoInteractor;
import com.kelsos.mbrc.interactors.TrackLyricsInteractor;
import com.kelsos.mbrc.interactors.VolumeInteractor;
import com.kelsos.mbrc.repository.PlayerRepository;
import com.kelsos.mbrc.repository.TrackRepository;
import com.kelsos.mbrc.utilities.MainThreadBus;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import rx.functions.Action0;
import rx.schedulers.Schedulers;

@Singleton
public class SocketMessageHandler {

  private Map<String, Action0> actions;
  @Inject private VolumeInteractor volumeInteractor;
  @Inject private PlayerRepository playerRepository;
  @Inject private TrackRepository trackRepository;
  @Inject private TrackCoverInteractor coverInteractor;
  @Inject private TrackLyricsInteractor lyricsInteractor;
  @Inject private TrackInfoInteractor trackInfoInteractor;
  @Inject private RepeatInteractor repeatInteractor;
  @Inject private PlayerStateInteractor playerStateInteractor;

  @Inject public SocketMessageHandler(MainThreadBus bus) {
    bus.register(this);
    actions = new HashMap<>();
    actions.put(SocketNotification.VOLUME, () ->  {
      volumeInteractor.execute().subscribeOn(Schedulers.io()).subscribe((volume) -> {
        playerRepository.setVolume(volume);
        bus.post(VolumeChangeEvent.newInstance(volume));
      });
    });

    actions.put(SocketNotification.COVER, () -> {
      coverInteractor.execute(true).subscribeOn(Schedulers.io()).subscribe((cover) -> {
        bus.post(CoverChangedEvent.newBuilder().withCover(cover).build());
      });
    });

    actions.put(SocketNotification.LYRICS, () -> {
      lyricsInteractor.execute(true).subscribeOn(Schedulers.io()).subscribe((lyrics) -> {
        bus.post(LyricsChangedEvent.newBuilder().withLyrics(lyrics).build());
      });

    });

    actions.put(SocketNotification.TRACK, () -> {
      trackInfoInteractor.execute(true).subscribeOn(Schedulers.io()).subscribe((trackInfo) -> {
        bus.post(TrackInfoChangeEvent.newBuilder().withTrackInfo(trackInfo).build());
      });
    });

    actions.put(SocketNotification.PLAY_STATUS, () -> {
      playerStateInteractor.execute(true).subscribeOn(Schedulers.io()).subscribe(playState -> {
        bus.post(PlayStateChange.newBuilder().withState(playState).build());
      });
    });

    actions.put(SocketNotification.REPEAT, () -> {
      repeatInteractor.execute(true).subscribeOn(Schedulers.io()).subscribe(s -> {
        bus.post(RepeatChange.newBuilder().withMode(s).build());
      });
    });
  }

  @Subscribe public void onWebSocketMessage(WebSocketMessage message) {
    final Action0 action = actions.get(message.getMessage());
    if (action != null) {
      action.call();
    }
  }
}
