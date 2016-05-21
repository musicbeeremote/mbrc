package com.kelsos.mbrc.utilities;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import timber.log.Timber;

public class DelayTimer {
  private final ScheduledExecutorService mScheduler = Executors.newScheduledThreadPool(1);
  private int delay;
  private boolean running;
  private ScheduledFuture mFuture;
  private TimerFinishEvent mTimerFinishListener;

  /**
   * Delay timer constructor, with a delay parameter
   *
   * @param delay Number of seconds to wait.
   */
  @SuppressWarnings("unused") public DelayTimer(int delay) {
    this.delay = delay;
    mTimerFinishListener = null;
  }

  /**
   * Delay timer constructor with delay parameter and a listener for the execute event.
   *
   * @param delay Number of seconds to wait.
   * @param listener The event listener that will run after the delay
   */
  public DelayTimer(int delay, TimerFinishEvent listener) {
    this.delay = delay;
    mTimerFinishListener = listener;
  }

  /**
   * The method is used to request the timer
   */
  public void start() {
    stop();
    final DelayTimerTask task = new DelayTimerTask();
    mFuture = mScheduler.schedule(task, delay, TimeUnit.SECONDS);
    running = true;
  }

  /**
   * The method is used to stop the timer.
   */
  public void stop() {
    if (mFuture != null) {
      mFuture.cancel(true);
      Timber.d("stopping delay timer");
    }
    running = false;
  }

  /**
   * This method returns the status of the delay timer.
   *
   * @return true if the timer is running and false if not.
   */
  @SuppressWarnings("unused") public boolean isRunning() {
    return running;
  }

  /**
   * Sets an event listener that listens for the timer finish event.
   *
   * @param listener The event listener
   */
  @SuppressWarnings("unused") public void setTimerFinishEventListener(TimerFinishEvent listener) {
    mTimerFinishListener = listener;
  }

  private void onTimerFinish() {
    if (mTimerFinishListener != null) {
      mTimerFinishListener.onTimerFinish();
    }
  }

  /**
   * This interface represents the TimerFinishEvent
   * The abstract method onTimerFinish() fires when the countdown finishes.
   */
  public interface TimerFinishEvent {
    void onTimerFinish();
  }

  private class DelayTimerTask implements Runnable {
    @Override public void run() {
      stop();
      onTimerFinish();
      Timber.d("delay timer tick");
    }
  }
}
