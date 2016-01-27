package com.kelsos.mbrc.viewmodels;

import android.graphics.Bitmap;
import android.support.annotation.IntRange;
import com.kelsos.mbrc.annotations.PlayerState;
import com.kelsos.mbrc.annotations.Repeat;
import com.kelsos.mbrc.annotations.Shuffle;
import com.kelsos.mbrc.domain.TrackPosition;
import com.kelsos.mbrc.dto.track.TrackInfo;

public class MainViewModelImpl implements MainViewModel {
  private TrackInfo trackInfo;
  private Bitmap cover;
  private TrackPosition position;
  private float rating;
  @Shuffle.State private String shuffle;
  @PlayerState.State private String playState;
  @Repeat.Mode private String repeat;
  private boolean muted;
  @IntRange(from = -1, to = 100) private int volume;
  private boolean loaded;

  public MainViewModelImpl() {
    trackInfo = new TrackInfo();
    cover = null;
    position = new TrackPosition(0, 0);
    rating = 0;
    shuffle = Shuffle.UNDEF;
    playState = PlayerState.UNDEFINED;
    repeat = Repeat.UNDEFINED;
    muted = false;
    volume = -1;
    loaded = false;
  }

  @Shuffle.State @Override public String getShuffle() {
    return shuffle;
  }

  @Override public void setShuffle(@Shuffle.State String shuffle) {
    this.shuffle = shuffle;
  }

  @PlayerState.State @Override public String getPlayState() {
    return playState;
  }

  @Override public void setPlayState(@PlayerState.State String playState) {
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

  @Override public TrackInfo getTrackInfo() {
    return trackInfo;
  }

  @Override public void setTrackInfo(TrackInfo trackInfo) {
    this.trackInfo = trackInfo;
  }

  @Override public Bitmap getTrackCover() {
    return cover;
  }

  @Override public void setTrackCover(Bitmap cover) {
    this.cover = cover;
  }

  @Override public TrackPosition getPosition() {
    return position;
  }

  @Override public void setPosition(TrackPosition position) {
    this.position = position;
  }

  @Override public float getRating() {
    return rating;
  }

  @Override public void setRating(float rating) {
    this.rating = rating;
  }

  @IntRange(from = -1, to = 100) @Override public int getVolume() {
    return volume;
  }

  @Override public void setVolume(@IntRange(from = -1, to = 100) int volume) {
    this.volume = volume;
  }

  @Override public boolean isLoaded() {
    return loaded;
  }

  @Override public void loadComplete() {
    loaded = true;
  }
}
