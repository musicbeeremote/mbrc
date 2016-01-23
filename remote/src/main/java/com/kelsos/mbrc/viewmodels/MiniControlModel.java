package com.kelsos.mbrc.viewmodels;

import android.graphics.Bitmap;
import com.kelsos.mbrc.annotations.PlayerState;

public class MiniControlModel {
  private Bitmap cover;
  private String title;
  private String artist;
  private String playerState;

  public void setCover(Bitmap cover) {
    this.cover = cover;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  public void setPlayerState(@PlayerState.State String playerState) {
    this.playerState = playerState;
  }
}
