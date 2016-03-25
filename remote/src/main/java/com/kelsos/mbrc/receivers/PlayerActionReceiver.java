package com.kelsos.mbrc.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.PlayerAction;
import com.kelsos.mbrc.interactors.PlayerInteractor;
import com.kelsos.mbrc.utilities.RemoteViewIntentBuilder;
import roboguice.RoboGuice;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PlayerActionReceiver extends BroadcastReceiver {
  @Inject private PlayerInteractor playerInteractor;

  public PlayerActionReceiver() {

  }

  public IntentFilter getIntentFilter() {
    IntentFilter filter = new IntentFilter();
    filter.addAction(RemoteViewIntentBuilder.REMOTE_PLAY_PRESSED);
    filter.addAction(RemoteViewIntentBuilder.REMOTE_NEXT_PRESSED);
    filter.addAction(RemoteViewIntentBuilder.REMOTE_CLOSE_PRESSED);
    filter.addAction(RemoteViewIntentBuilder.REMOTE_PREVIOUS_PRESSED);
    return filter;
  }

  @Override public void onReceive(Context context, Intent intent) {
    if (playerInteractor == null) {
      RoboGuice.getInjector(context).injectMembers(this);
    }

    Timber.i("Received intent %s", intent.getAction());

    @PlayerAction.Action String action = null;

    if (RemoteViewIntentBuilder.REMOTE_PLAY_PRESSED.equals(intent.getAction())) {
      action = PlayerAction.PLAY_PLAUSE;
    } else if (RemoteViewIntentBuilder.REMOTE_NEXT_PRESSED.equals(intent.getAction())) {
      action = PlayerAction.NEXT;
    } else if (RemoteViewIntentBuilder.REMOTE_CLOSE_PRESSED.equals(intent.getAction())) {
      // TODO: 3/15/16 Terminate application or close notification
    } else if (RemoteViewIntentBuilder.REMOTE_PREVIOUS_PRESSED.equals(intent.getAction())) {
      action = PlayerAction.PREVIOUS;
    }

    if (action != null) {
      perform(action);
    }
  }

  private void perform(@PlayerAction.Action String action) {
    playerInteractor.performAction(action).subscribeOn(Schedulers.io()).subscribe(response -> {
      Timber.v("Action -> %s", response);
    }, t -> Timber.e(t, "failed"));
  }
}
