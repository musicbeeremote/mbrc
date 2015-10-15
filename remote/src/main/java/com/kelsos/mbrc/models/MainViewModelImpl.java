package com.kelsos.mbrc.models;

import android.graphics.Bitmap;

import com.kelsos.mbrc.dto.player.PlaybackState;
import com.kelsos.mbrc.dto.track.Position;
import com.kelsos.mbrc.dto.track.Rating;
import com.kelsos.mbrc.dto.player.Repeat;
import com.kelsos.mbrc.dto.player.Shuffle;
import com.kelsos.mbrc.dto.track.TrackInfo;
import com.kelsos.mbrc.dto.player.Volume;

public class MainViewModelImpl implements MainViewModel {
  private TrackInfo trackInfo;
  private Bitmap cover;
  private Position position;
  private Rating rating;
  private Shuffle shuffle;
  private PlaybackState playbackState;
  private Repeat repeat;
  private boolean muted;
  private Volume volume;

  @Override public Shuffle getShuffle() {
    return shuffle;
  }

  @Override public void setShuffle(Shuffle shuffle) {
    this.shuffle = shuffle;
  }

  @Override public PlaybackState getPlaybackState() {
    return playbackState;
  }

  @Override public void setPlaybackState(PlaybackState playbackState) {
    this.playbackState = playbackState;
  }

  @Override public Repeat getRepeat() {
    return repeat;
  }

  @Override public void setRepeat(Repeat repeat) {
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
