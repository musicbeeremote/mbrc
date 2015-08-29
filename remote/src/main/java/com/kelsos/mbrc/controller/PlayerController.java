package com.kelsos.mbrc.controller;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.PlaybackAction;
import com.kelsos.mbrc.data.model.PlayerModel;
import com.kelsos.mbrc.enums.PlayState;
import com.kelsos.mbrc.events.ui.ShuffleChange;
import com.kelsos.mbrc.rest.RemoteApi;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PlayerController {
  private RemoteApi api;
  private PlayerModel model;

  @Inject public PlayerController(RemoteApi api, PlayerModel model) {
    this.api = api;
    this.model = model;
    this.init();
  }

  private void init() {
    api.getShuffleState().subscribeOn(Schedulers.io()).subscribe(stateResponse -> {
      model.setShuffleState(stateResponse.getState());
    });
  }

  public PlayState getPlayState() {
    return model.getPlayState();
  }

  public void onPlayPressed() {
    api.performPlayerAction(PlaybackAction.PLAY_PLAUSE)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(successResponse -> {
        });
  }

  public void onPreviousPressed() {
    api.performPlayerAction(PlaybackAction.PREVIOUS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(successResponse -> {
        });
  }

  public void onNextPressed() {
    api.performPlayerAction(PlaybackAction.NEXT)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(successResponse -> {
        });
  }

  public void onStopPressed() {
    api.performPlayerAction(PlaybackAction.STOP)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(successResponse -> {
        });
  }

  public void onMutePressed() {
    api.getMuteState()
        .flatMap(stateResponse -> api.updateMuteState(!stateResponse.isEnabled()))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(successResponse -> {
        });
  }

  public void onShufflePressed() {
    api.toggleShuffleState()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(successResponse -> {
        });
  }

  public void onRepeatPressed() {
    api.changeRepeatMode()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(successResponse -> {
        });
  }

  @ShuffleChange.ShuffleState public String getShuffleState() {
    return model.getShuffleState();
  }

}
