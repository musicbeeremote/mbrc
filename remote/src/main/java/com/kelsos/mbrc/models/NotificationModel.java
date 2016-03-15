package com.kelsos.mbrc.models;

import android.graphics.Bitmap;
import com.kelsos.mbrc.annotations.PlayerState;
import com.kelsos.mbrc.dto.track.TrackInfo;

public class NotificationModel {
  private TrackInfo trackInfo;
  private Bitmap cover;
  @PlayerState.State private String playState;

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

  @PlayerState.State public String getPlayState() {
    return playState;
  }

  public void setPlayState(@PlayerState.State String playState) {
    this.playState = playState;
  }
}
