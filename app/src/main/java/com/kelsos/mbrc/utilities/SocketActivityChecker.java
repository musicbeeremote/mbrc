package com.kelsos.mbrc.utilities;

import android.support.annotation.NonNull;
import com.google.inject.Singleton;
import java.util.concurrent.TimeUnit;
import rx.Completable;
import rx.Subscription;
import timber.log.Timber;

@Singleton
public class SocketActivityChecker {
  private static final int DELAY = 40;
  private Subscription subscription;
  private PingTimeoutListener pingTimeoutListener;

  public SocketActivityChecker() {

  }

  public void start() {
    Timber.v("Starting activity checker");
    subscription = getSubscribe();
  }

  @NonNull
  private Subscription getSubscribe() {
    return Completable.timer(DELAY, TimeUnit.SECONDS).subscribe(throwable -> Timber.v("Subscription failed"), () -> {
      Timber.v("Ping was more than %d seconds ago", DELAY);
      if (pingTimeoutListener == null) {
        return;
      }
      pingTimeoutListener.onTimeout();
    });
  }

  public void stop() {
    Timber.v("Stopping activity checker");
    unsubscribe();
  }

  public void ping() {
    Timber.v("Received ping");
    unsubscribe();
    subscription = getSubscribe();
  }

  private void unsubscribe() {
    if (subscription != null && !subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  public void setPingTimeoutListener(PingTimeoutListener pingTimeoutListener) {
    this.pingTimeoutListener = pingTimeoutListener;
  }

  public interface PingTimeoutListener {
    void onTimeout();
  }
}
