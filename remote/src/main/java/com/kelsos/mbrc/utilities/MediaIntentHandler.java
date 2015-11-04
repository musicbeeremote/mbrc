package com.kelsos.mbrc.utilities;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.squareup.otto.Bus;

@Singleton public class MediaIntentHandler {

  public static final int DOUBLE_CLICK_INTERVAL = 350;
  private final Bus bus;
  private long previousClick;

  @Inject public MediaIntentHandler(Bus bus) {
    this.bus = bus;
    previousClick = 0;
  }

  public boolean handleMediaIntent(Intent mediaIntent) {
    boolean result = false;

    //noinspection StatementWithEmptyBody
    if (mediaIntent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
      // Handle somehow
    } else if (mediaIntent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
      final Bundle extras = mediaIntent.getExtras();
      final KeyEvent keyEvent = (KeyEvent) extras.get(Intent.EXTRA_KEY_EVENT);

      if (keyEvent.getAction() != KeyEvent.ACTION_DOWN) {
        return false;
      }

      switch (keyEvent.getKeyCode()) {
        case KeyEvent.KEYCODE_HEADSETHOOK:
          long currentClick = System.currentTimeMillis();
          if (currentClick - previousClick < DOUBLE_CLICK_INTERVAL) {

            break;
          }
          previousClick = currentClick;
        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:

          break;
        case KeyEvent.KEYCODE_MEDIA_PLAY:

          break;
        case KeyEvent.KEYCODE_MEDIA_PAUSE:

          break;
        case KeyEvent.KEYCODE_MEDIA_STOP:

          break;
        case KeyEvent.KEYCODE_MEDIA_NEXT:

          break;
        case KeyEvent.KEYCODE_MEDIA_PREVIOUS:

          break;
        default:
          break;
      }
    }
    return result;
  }

}
