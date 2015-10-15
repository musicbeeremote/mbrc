package com.kelsos.mbrc.domain;

public class TrackPosition {

  private int currentSeconds;
  private int currentMinutes;
  private int current;
  private int totalSeconds;
  private int totalMinutes;
  private int total;
  public TrackPosition(int current, int total) {
    this.current = current;
    current /= 1000;
    currentMinutes = current / 60;
    currentSeconds = current % 60;
    this.total = total;
    total /= 1000;
    totalMinutes = total / 60;
    totalSeconds = total % 60;
  }

  public int getCurrent() {
    return current;
  }

  public int getTotal() {
    return total;
  }

  public int getCurrentSeconds() {
    return currentSeconds;
  }

  public int getCurrentMinutes() {
    return currentMinutes;
  }

  public int getTotalSeconds() {
    return totalSeconds;
  }

  public int getTotalMinutes() {
    return totalMinutes;
  }
}
