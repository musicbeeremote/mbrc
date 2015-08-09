package com.kelsos.mbrc.controller;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.PlaybackAction;
import com.kelsos.mbrc.rest.RemoteApi;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainViewController {
  private RemoteApi api;

  @Inject public MainViewController(RemoteApi api) {
    this.api = api;
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
}
