package com.kelsos.mbrc.utilities;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import com.google.inject.Inject;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.events.MessageEvent;
import com.squareup.otto.Bus;
import roboguice.receiver.RoboBroadcastReceiver;

public class MediaButtonReceiver extends RoboBroadcastReceiver {
  @Inject Bus bus;

  private void postAction(UserAction action) {
    bus.post(new MessageEvent(ProtocolEventType.UserAction, action));
  }


  @Override protected void handleReceive(Context context, Intent intent) {
    if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
      // Handle somehow
    } else if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
      KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);

      if (keyEvent.getAction() != KeyEvent.ACTION_DOWN) {
        return;
      }

      switch (keyEvent.getKeyCode()) {
        case KeyEvent.KEYCODE_HEADSETHOOK:
        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
          postAction(new UserAction(Protocol.PlayerPlayPause, true));
          break;
        case KeyEvent.KEYCODE_MEDIA_PLAY:
          //
          break;
        case KeyEvent.KEYCODE_MEDIA_PAUSE:
          //
          break;
        case KeyEvent.KEYCODE_MEDIA_STOP:
          postAction(new UserAction(Protocol.PlayerStop, true));
          break;
        case KeyEvent.KEYCODE_MEDIA_NEXT:
          postAction(new UserAction(Protocol.PlayerNext, true));
          break;
        case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
          // TODO: ensure that doing this in rapid succession actually plays the
          // previous song
          postAction(new UserAction(Protocol.PlayerPrevious, true));
          break;
        default:
          break;
      }
    }
  }
}
