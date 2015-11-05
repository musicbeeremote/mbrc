package com.kelsos.mbrc.models;

import android.graphics.Bitmap;

import com.kelsos.mbrc.annotations.Repeat;
import com.kelsos.mbrc.dto.player.PlayState;
import com.kelsos.mbrc.dto.player.Shuffle;
import com.kelsos.mbrc.dto.player.Volume;
import com.kelsos.mbrc.dto.track.Position;
import com.kelsos.mbrc.dto.track.Rating;
import com.kelsos.mbrc.dto.track.TrackInfo;

public class MainViewModelImpl implements MainViewModel {
  private TrackInfo trackInfo;
  private Bitmap cover;
  private Position position;
  private Rating rating;
  private Shuffle shuffle;
  private PlayState playState;
  @Repeat.Mode private String repeat;
  private boolean muted;
  private Volume volume;

  @Override public Shuffle getShuffle() {
    return shuffle;
  }

  @Override public void setShuffle(Shuffle shuffle) {
    this.shuffle = shuffle;
  }

  @Override public PlayState getPlayState() {
    return playState;
  }

  @Override public void setPlayState(PlayState playState) {
    this.playState = playState;
  }

  @Override @Repeat.Mode public String getRepeat() {
    return repeat;
  }

  @Override public void setRepeat(@Repeat.Mode String repeat) {
    this.repeat = repeat;
  }

  @Override public boolean isMuted() {
    return muted;
  }

  @Override public void setMuted(boolean muted) {
    this.muted = muted;
  }

  @Override
  public TrackInfo getTrackInfo() {
    return trackInfo;
  }

  @Override
  public void setTrackInfo(TrackInfo trackInfo) {
    this.trackInfo = trackInfo;
  }

  @Override
  public Bitmap getTrackCover() {
    return cover;
  }

  @Override
  public void setTrackCover(Bitmap cover) {
    this.cover = cover;
  }

  @Override
  public Position getPosition() {
    return position;
  }

  @Override
  public void setPosition(Position position) {
    this.position = position;
  }

  @Override
  public Rating getRating() {
    return rating;
  }

  @Override
  public void setRating(Rating rating) {
    this.rating = rating;
  }

  @Override public Volume getVolume() {
    return volume;
  }

  @Override public void setVolume(Volume volume) {
    this.volume = volume;
  }
}
