package com.kelsos.mbrc.model;

import android.graphics.Bitmap;

import com.kelsos.mbrc.annotations.PlayerState;
import com.kelsos.mbrc.annotations.PlayerState.State;
import com.kelsos.mbrc.domain.TrackInfo;
import javax.inject.Inject;

public class NotificationModel {
  private TrackInfo trackInfo;
  private Bitmap cover;
  @State
  private String playState;

  @Inject
  public NotificationModel() {
    playState = PlayerState.STOPPED;
  }

  public TrackInfo getTrackInfo() {
    return trackInfo;
  }

  public void setTrackInfo(TrackInfo trackInfo) {
    this.trackInfo = trackInfo;
  }

  public Bitmap getCover() {
    return cover;
  }

  public void setCover(Bitmap cover) {
    this.cover = cover;
  }

  @State
  public String getPlayState() {
    return playState;
  }

  public void setPlayState(@State String playState) {
    this.playState = playState;
  }
}
