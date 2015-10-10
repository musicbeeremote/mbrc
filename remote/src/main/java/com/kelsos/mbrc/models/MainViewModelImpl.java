package com.kelsos.mbrc.models;

import android.graphics.Bitmap;

import com.kelsos.mbrc.dto.Position;
import com.kelsos.mbrc.dto.Rating;
import com.kelsos.mbrc.dto.TrackInfo;

public class MainViewModelImpl implements MainViewModel {
  private TrackInfo trackInfo;
  private Bitmap cover;
  private Position position;
  private Rating rating;

  @Override
  public TrackInfo getTrackInfo() {
    return trackInfo;
  }

  @Override
  public Bitmap getTrackCover() {
    return cover;
  }

  @Override
  public Position getPosition() {
    return position;
  }

  @Override
  public Rating getRating() {
    return rating;
  }

  @Override
  public void setTrackInfo(TrackInfo trackInfo) {
    this.trackInfo = trackInfo;
  }

  @Override
  public void setTrackCover(Bitmap cover) {
    this.cover = cover;
  }

  @Override
  public void setPosition(Position position) {
    this.position = position;
  }

  @Override
  public void setRating(Rating rating) {
    this.rating = rating;
  }
}
