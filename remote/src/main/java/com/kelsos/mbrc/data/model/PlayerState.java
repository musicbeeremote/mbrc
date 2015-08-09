package com.kelsos.mbrc.data.model;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.constants.EventType;
import com.kelsos.mbrc.enums.PlayState;
import com.kelsos.mbrc.events.Events;
import com.kelsos.mbrc.events.Message;
import com.kelsos.mbrc.events.actions.ButtonPressedEvent.Button;
import com.kelsos.mbrc.net.Notification;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.rest.responses.SuccessResponse;
import com.kelsos.mbrc.utilities.Logger;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

@Singleton public class PlayerState {
  public static final int MAX_VOLUME = 100;
  public static final int LOWEST_NOT_ZERO = 10;
  public static final int GREATEST_NOT_MAX = 90;
  public static final int STEP = 10;
  public static final int MOD = 10;
  public static final int BASE = 0;
  public static final int LIMIT = 5;
  public static final String ALL = "All";
  public static final String PLAYING = "playing";
  public static final String PAUSED = "paused";
  public static final String STOPPED = "stopped";
  private final RemoteApi api;
  private final BehaviorSubject<PlayState> playerStateBehaviorSubject;
  private final BehaviorSubject<Integer> volumeBehaviourSubject;
  private final BehaviorSubject<Boolean> shuffleStateBehaviourSubject;
  private final BehaviorSubject<Boolean> repeatStateBehaviourSubject;
  private final BehaviorSubject<Boolean> scrobblingStateBehaviourSubject;
  boolean repeatActive;
  boolean shuffleActive;
  boolean isScrobblingActive;
  boolean isMuteActive;
  private int volume;
  private PlayState playState;

  @Inject public PlayerState(RemoteApi api) {
    this.api = api;
    playerStateBehaviorSubject = BehaviorSubject.create(PlayState.STOPPED);
    volumeBehaviourSubject = BehaviorSubject.create(BASE);
    shuffleStateBehaviourSubject = BehaviorSubject.create(false);
    repeatStateBehaviourSubject = BehaviorSubject.create(false);
    scrobblingStateBehaviourSubject = BehaviorSubject.create(false);

    getMessageObservable(Notification.VOLUME_CHANGED).flatMap(resp -> api.getVolume())
        .subscribe(resp -> setVolume(resp.getValue()), Logger::logThrowable);

    getMessageObservable(Notification.PLAY_STATUS_CHANGED).flatMap(resp -> api.getPlaystate())
        .subscribe(resp -> setPlayState(resp.getValue()), Logger::logThrowable);

    getMessageObservable(EventType.KEY_VOLUME_UP).subscribe(msg -> increaseVolume(),
        Logger::logThrowable);

    getMessageObservable(EventType.KEY_VOLUME_DOWN).subscribe(msg -> reduceVolume(),
        Logger::logThrowable);

    subscribeToButtonEvent(Button.PREVIOUS, api.playPrevious());
    subscribeToButtonEvent(Button.NEXT, api.playNext());
    subscribeToButtonEvent(Button.STOP, api.playbackStop());
    subscribeToButtonEvent(Button.PLAYPAUSE, api.playPause());
    requestPlayerStatus();
    subscribeToRepeatChanges();
    subscribeToShuffleChanges();
    subscribeToRepeatButtonPress();
    subscribeToShuffleButttonPress();
    subscribeToVolumeChangeEvent();
  }

  private Observable<Message> getMessageObservable(String type) {
    return Events.messages.subscribeOn(Schedulers.io())
        .observeOn(Schedulers.immediate())
        .filter(msg -> msg.getType().equals(type));
  }

  private void subscribeToVolumeChangeEvent() {
    getMessageObservable(Notification.VOLUME_CHANGED).flatMap(msg -> api.getVolume())
        .subscribe(resp -> setVolume(resp.getValue()), Logger::logThrowable);
  }

  private void subscribeToShuffleButttonPress() {
    Events.buttonPressedSub.subscribeOn(Schedulers.io())
        .filter(event -> event.getType().equals(Button.SHUFFLE))
        .flatMap(event -> api.toggleShuffleState())
        .subscribe(r -> setShuffleState(r.isEnabled()), Logger::logThrowable);
  }

  private void subscribeToRepeatButtonPress() {
    Events.buttonPressedSub.subscribeOn(Schedulers.io())
        .filter(event -> event.getType().equals(Button.REPEAT))
        .flatMap(event -> api.changeRepeatMode())
        .subscribe(r -> setRepeatState(r.getValue()), Logger::logThrowable);
  }

  private void subscribeToRepeatChanges() {
    getMessageObservable(Notification.REPEAT_STATUS_CHANGED).flatMap(message -> api.getRepeatMode())
        .subscribe(response -> setRepeatState(response.getValue()), Logger::logThrowable);
  }

  private void subscribeToShuffleChanges() {
    getMessageObservable(Notification.SHUFFLE_STATUS_CHANGED).flatMap(
        message -> api.getShuffleState())
        .subscribe(response -> setShuffleState(response.isEnabled()), Logger::logThrowable);
  }

  private void requestPlayerStatus() {
    api.getPlayerStatus()
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.immediate())
        .subscribe(resp -> {
          setMuteState(resp.isMute());
          setScrobbleState(resp.isScrobble());
          setShuffleState(resp.isShuffle());
          setRepeatState(resp.getRepeat());
          setVolume(resp.getVolume());
          setPlayState(resp.getState());
        }, Logger::logThrowable);
  }

  private void subscribeToButtonEvent(Button button, Observable<SuccessResponse> apiRequest) {
    Events.buttonPressedSub.subscribeOn(Schedulers.io())
        .filter(event -> event.getType().equals(button))
        .flatMap(event -> apiRequest)
        .subscribe(r -> {
        }, Logger::logThrowable);
  }

  public void setRepeatState(String repeatButtonActive) {
    repeatActive = ALL.equalsIgnoreCase(repeatButtonActive);
    onRepeatStateChange(repeatActive);
  }

  private void onRepeatStateChange(boolean repeatActive) {
    repeatStateBehaviourSubject.onNext(repeatActive);
  }

  public void setShuffleState(boolean shuffleButtonActive) {
    shuffleActive = shuffleButtonActive;
    onShuffleStateChange(shuffleActive);
  }

  public void setScrobbleState(boolean scrobbleButtonActive) {
    isScrobblingActive = scrobbleButtonActive;
    onScrobbleStateChange(isScrobblingActive);
  }

  private void onScrobbleStateChange(boolean isScrobblingActive) {
    scrobblingStateBehaviourSubject.onNext(isScrobblingActive);
  }

  public void setMuteState(boolean isMuteActive) {
    this.isMuteActive = isMuteActive;
  }

  public void setPlayState(String playState) {
    PlayState newState = PlayState.UNDEFINED;
    if (PLAYING.equalsIgnoreCase(playState)) {
      newState = PlayState.PLAYING;
    } else if (STOPPED.equalsIgnoreCase(playState)) {
      newState = PlayState.STOPPED;
    } else if (PAUSED.equalsIgnoreCase(playState)) {
      newState = PlayState.PAUSED;
    }
    this.playState = newState;
    onPlayStateChange(newState);
  }

  private void reduceVolume() {
    if (volume >= LOWEST_NOT_ZERO) {
      int mod = volume % MOD;
      int newVolume;

      if (mod == BASE) {
        newVolume = volume - STEP;
      } else if (mod < LIMIT) {
        newVolume = volume - (STEP + mod);
      } else {
        newVolume = volume - mod;
      }

      updateVolume(newVolume);
    }
  }

  private void updateVolume(int newVolume) {
    api.updateVolume(newVolume)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.immediate())
        .subscribe(resp -> setVolume(resp.getValue()));
  }

  private void increaseVolume() {
    if (volume <= GREATEST_NOT_MAX) {
      int mod = volume % MOD;
      int newVolume;

      if (mod == BASE) {
        newVolume = volume + STEP;
      } else if (mod < LIMIT) {
        newVolume = volume + (STEP - mod);
      } else {
        newVolume = volume + ((2 * STEP) - mod);
      }

      updateVolume(newVolume);
    }
  }

  public Observable<Boolean> observeRepeatState() {
    return repeatStateBehaviourSubject.asObservable().distinctUntilChanged();
  }

  private void onShuffleStateChange(boolean enabled) {
    shuffleStateBehaviourSubject.onNext(enabled);
  }

  public Observable<Boolean> observeShuffleState() {
    return shuffleStateBehaviourSubject.asObservable().distinctUntilChanged();
  }

  public Observable<Boolean> observeScrobbleState() {
    return scrobblingStateBehaviourSubject.asObservable().distinctUntilChanged();
  }

  private void onPlayStateChange(PlayState playState) {
    playerStateBehaviorSubject.onNext(playState);
  }

  public Observable<PlayState> observePlaystate() {
    return playerStateBehaviorSubject.asObservable().distinctUntilChanged();
  }

  private void onVolumeChange(int volume) {
    volumeBehaviourSubject.onNext(volume);
  }

  public Observable<Integer> observeVolume() {
    return volumeBehaviourSubject.asObservable().distinctUntilChanged();
  }

  public void setVolume(int volume) {
    if (volume != this.volume) {
      this.volume = volume;
      onVolumeChange(volume);
    }
  }
}
