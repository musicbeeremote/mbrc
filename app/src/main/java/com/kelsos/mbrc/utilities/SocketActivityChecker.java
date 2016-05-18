package com.kelsos.mbrc.utilities;

import android.support.annotation.NonNull;
import com.google.inject.Singleton;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import rx.Completable;
import rx.Subscription;
import timber.log.Timber;

@Singleton public class SocketActivityChecker {
  private AtomicLong lastPing;
  private Subscription subscription;

  private PingTimeoutListener pingTimeoutListener;

  public SocketActivityChecker() {
    lastPing = new AtomicLong();
  }

  public void start() {
    lastPing.set(System.currentTimeMillis());
    Timber.v("Starting activity checker");
    subscription = getSubscribe();
  }

  @NonNull private Subscription getSubscribe() {
    return Completable.timer(20, TimeUnit.SECONDS).subscribe(throwable -> {
      Timber.v("failed");
    }, () -> {
      Timber.v("Ping was more than 20 seconds ago");
      if (pingTimeoutListener == null) {
        return;
      }
      pingTimeoutListener.onTimeout();
    });
  }

  public void stop() {
    Timber.v("Stopping activity checker");
  }

  public void ping() {
    Timber.v("Received ping");
    long millis = System.currentTimeMillis();
    lastPing.set(millis);

    if (subscription != null && !subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }

    subscription = getSubscribe();
  }

  public void setPingTimeoutListener(PingTimeoutListener pingTimeoutListener) {
    this.pingTimeoutListener = pingTimeoutListener;
  }

  public interface PingTimeoutListener {
    void onTimeout();
  }
}
