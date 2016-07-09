package com.kelsos.mbrc.helper;

import android.widget.SeekBar;

import com.jakewharton.rxrelay.PublishRelay;

import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

public class VolumeChangeHelper implements SeekBar.OnSeekBarChangeListener {

  private boolean userChangingVolume;
  private Action1<Integer> action;
  private PublishRelay<Integer> volumeRelay = PublishRelay.create();

  public VolumeChangeHelper(Action1<Integer> action) {
    this.action = action;
    this.userChangingVolume = false;
    volumeRelay.throttleLast(600, TimeUnit.MILLISECONDS).subscribe(this::onVolumeChange);
  }

  public boolean isUserChangingVolume() {
    return userChangingVolume;
  }

  @Override
  public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
    if (fromUser) {
      volumeRelay.call(value);
    }
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {
    userChangingVolume = true;
  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {
    userChangingVolume = false;
  }

  private void onVolumeChange(int change) {
    if (action != null) {
      action.call(change);
    }
  }
}
