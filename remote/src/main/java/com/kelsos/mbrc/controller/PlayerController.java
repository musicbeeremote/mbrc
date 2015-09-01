package com.kelsos.mbrc.controller;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.PlaybackAction;
import com.kelsos.mbrc.annotations.RepeatMode;
import com.kelsos.mbrc.annotations.ShuffleState;
import com.kelsos.mbrc.data.model.PlayerModel;
import com.kelsos.mbrc.enums.PlayState;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.rest.requests.ChangeStateRequest;
import com.kelsos.mbrc.rest.requests.RepeatRequest;
import com.kelsos.mbrc.rest.requests.ShuffleRequest;
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
    api.updateMuteState(new ChangeStateRequest().setEnabled(null))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(successResponse -> {
        });
  }

  public void onShufflePressed() {
    api.updateShuffleState(new ShuffleRequest().setStatus(ShuffleState.TOGGLE))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(successResponse -> {
          model.setShuffleState(successResponse.getState());
        });
  }

  public void onRepeatPressed() {
    api.updateRepeatState(new RepeatRequest().setMode(RepeatMode.CHANGE))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(successResponse -> {
        });
  }

  @ShuffleState public String getShuffleState() {
    return model.getShuffleState();
  }
}
