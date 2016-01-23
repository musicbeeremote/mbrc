package com.kelsos.mbrc.messaging;

import android.support.annotation.NonNull;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.cache.PlayerStateCache;
import com.kelsos.mbrc.constants.SocketNotification;
import com.kelsos.mbrc.dto.WebSocketMessage;
import com.kelsos.mbrc.events.ui.CoverChangedEvent;
import com.kelsos.mbrc.events.ui.LyricsChangedEvent;
import com.kelsos.mbrc.events.ui.MuteChangeEvent;
import com.kelsos.mbrc.events.ui.PlayStateChange;
import com.kelsos.mbrc.events.ui.RepeatChange;
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent;
import com.kelsos.mbrc.events.ui.VolumeChangeEvent;
import com.kelsos.mbrc.interactors.MuteInteractor;
import com.kelsos.mbrc.interactors.PlayerStateInteractor;
import com.kelsos.mbrc.interactors.RepeatInteractor;
import com.kelsos.mbrc.interactors.TrackCoverInteractor;
import com.kelsos.mbrc.interactors.TrackInfoInteractor;
import com.kelsos.mbrc.interactors.TrackLyricsInteractor;
import com.kelsos.mbrc.interactors.VolumeInteractor;
import com.kelsos.mbrc.interfaces.SocketAction;
import com.kelsos.mbrc.repository.TrackRepository;
import com.kelsos.mbrc.utilities.RxBus;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import roboguice.util.Ln;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

@Singleton public class SocketMessageHandler {

  private Map<String, SocketAction> actions;
  private Map<String, Subscription> activeSubscriptions;
  @Inject private VolumeInteractor volumeInteractor;
  @Inject private PlayerStateCache playerStateCache;
  @Inject private TrackRepository trackRepository;
  @Inject private TrackCoverInteractor coverInteractor;
  @Inject private TrackLyricsInteractor lyricsInteractor;
  @Inject private TrackInfoInteractor trackInfoInteractor;
  @Inject private RepeatInteractor repeatInteractor;
  @Inject private PlayerStateInteractor playerStateInteractor;
  @Inject private MuteInteractor muteInteractor;

  private PublishSubject<WebSocketMessage> volumeDebouncer = PublishSubject.create();

  @Inject public SocketMessageHandler(RxBus bus) {
    bus.register(WebSocketMessage.class, this::onWebSocketMessage);
    volumeDebouncer.debounce(1, TimeUnit.SECONDS).subscribe(this::handleVolume);

    actions = new HashMap<>();
    activeSubscriptions = new HashMap<>();
    actions.put(SocketNotification.VOLUME, getVolumeAction(bus));
    actions.put(SocketNotification.COVER, getCoverAction(bus));
    actions.put(SocketNotification.LYRICS, getLyricsAction(bus));
    actions.put(SocketNotification.TRACK, getTrackAction(bus));
    actions.put(SocketNotification.PLAY_STATUS, getPlayStatusAction(bus));
    actions.put(SocketNotification.REPEAT, getRepeatAction(bus));
    actions.put(SocketNotification.MUTE, getMuteAction(bus));
  }

  @NonNull public SocketAction getMuteAction(RxBus bus) {
    return () -> muteInteractor.getMuteState().subscribeOn(Schedulers.io()).subscribe(s -> {
      bus.post(MuteChangeEvent.newBuilder().withMute(s).build());
    });
  }

  @NonNull public SocketAction getRepeatAction(RxBus bus) {
    return () -> repeatInteractor.getRepeat().subscribeOn(Schedulers.io()).subscribe(s -> {
      bus.post(RepeatChange.newBuilder().withMode(s).build());
    });
  }

  @NonNull public SocketAction getPlayStatusAction(RxBus bus) {
    return () -> playerStateInteractor.getState().subscribeOn(Schedulers.io()).subscribe(playState -> {
      bus.post(PlayStateChange.newBuilder().withState(playState).build());
    });
  }

  @NonNull public SocketAction getTrackAction(RxBus bus) {
    return () -> trackInfoInteractor.execute(true).subscribeOn(Schedulers.io()).subscribe((trackInfo) -> {
      bus.post(TrackInfoChangeEvent.newBuilder().withTrackInfo(trackInfo).build());
    });
  }

  @NonNull public SocketAction getLyricsAction(RxBus bus) {
    return () -> lyricsInteractor.execute(true).subscribeOn(Schedulers.io()).subscribe((lyrics) -> {
      bus.post(LyricsChangedEvent.newBuilder().withLyrics(lyrics).build());
    });
  }

  @NonNull public SocketAction getCoverAction(RxBus bus) {
    return () -> coverInteractor.execute(true).subscribeOn(Schedulers.io()).subscribe((cover) -> {
      bus.post(CoverChangedEvent.newBuilder().withCover(cover).build());
    });
  }

  @NonNull public SocketAction getVolumeAction(RxBus bus) {
    return () -> volumeInteractor.getVolume()
        .subscribeOn(Schedulers.io())
        .doOnTerminate(() -> activeSubscriptions.remove(SocketNotification.VOLUME))
        .subscribe((volume) -> {
          bus.post(VolumeChangeEvent.newInstance(volume));
        });
  }

  private void onWebSocketMessage(WebSocketMessage message) {
    String action = message.getMessage();
    Ln.v("[Message] processing %s", action);

    Subscription actionSubscription = activeSubscriptions.get(action);
    if (actionSubscription != null && !actionSubscription.isUnsubscribed()) {
      Ln.v("There is already an active operation for the received action %s", action);
      return;
    }

    if (!SocketNotification.VOLUME.equals(action)) {
      handle(action);
    } else {
      volumeDebouncer.onNext(message);
    }
  }

  private void handleVolume(WebSocketMessage message) {
    handle(message.getMessage());
  }

  private void handle(String action) {
    SocketAction socketAction = actions.get(action);
    if (socketAction != null) {
      Subscription subscription = socketAction.call();
      activeSubscriptions.put(action, subscription);
    }
  }
}
