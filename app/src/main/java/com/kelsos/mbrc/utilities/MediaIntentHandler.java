package com.kelsos.mbrc.utilities;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.bus.RxBus;

@Singleton public class MediaIntentHandler {

  private static final int DOUBLE_CLICK_INTERVAL = 350;
  private final RxBus bus;
  private long previousClick;

  @Inject public MediaIntentHandler(RxBus bus) {
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
            result = postAction(new UserAction(Protocol.PlayerNext, true));
            break;
          }
          previousClick = currentClick;
        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
          result = postAction(new UserAction(Protocol.PlayerPlayPause, true));
          break;
        case KeyEvent.KEYCODE_MEDIA_PLAY:
          result = postAction(new UserAction(Protocol.PlayerPlay, true));
          break;
        case KeyEvent.KEYCODE_MEDIA_PAUSE:
          result = postAction(new UserAction(Protocol.PlayerPause, true));
          break;
        case KeyEvent.KEYCODE_MEDIA_STOP:
          result = postAction(new UserAction(Protocol.PlayerStop, true));
          break;
        case KeyEvent.KEYCODE_MEDIA_NEXT:
          result = postAction(new UserAction(Protocol.PlayerNext, true));
          break;
        case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
          result = postAction(new UserAction(Protocol.PlayerPrevious, true));
          break;
        default:
          break;
      }
    }
    return result;
  }

  private boolean postAction(UserAction action) {
    bus.post(new MessageEvent(ProtocolEventType.UserAction, action));
    return true;
  }
}
