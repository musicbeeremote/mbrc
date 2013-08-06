package com.kelsos.mbrc.others;

import android.util.Log;
import com.kelsos.mbrc.BuildConfig;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class DelayTimer {
    private int delay;
    private boolean isRunning;
    private final ScheduledExecutorService mScheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture mFuture;
    private TimerFinishEvent mTimerFinishListener;

    /**
     * Delay timer constructor, with a delay parameter
     * @param delay Number of seconds to wait.
     */
    public DelayTimer(int delay) {
        this.delay = delay;
        mTimerFinishListener = null;
    }

    /**
     * Delay timer constructor with delay parameter and a listener for the execute event.
     * @param delay Number of seconds to wait.
     * @param listener The event listener that will run after the delay
     */
    public DelayTimer(int delay, TimerFinishEvent listener) {
        this.delay = delay;
        mTimerFinishListener = listener;
    }

    /**
     * The method is used to start the timer
     */
    public void start() {
        stop();
        final DelayTimerTask task = new DelayTimerTask();
        mFuture = mScheduler.schedule(task, delay, TimeUnit.SECONDS);
        isRunning = true;
    }

    /**
     * The method is used to stop the timer.
     */
    public void stop() {
        if (mFuture!=null) {
            mFuture.cancel(true);
            if (BuildConfig.DEBUG) {
                Log.d("mbrc-log", "stopping delay timer");
            }

        }
        isRunning = false;
    }

    /**
     * This method returns the status of the delay timer.
     *
     * @return true if the timer is running and false if not.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Sets an event listener that listens for the timer finish event.
     *
     * @param listener The event listener
     */
    public void setTimerFinishEventListener(TimerFinishEvent listener) {
        mTimerFinishListener = listener;
    }

    private void onTimerFinish() {
        if (mTimerFinishListener != null)
            mTimerFinishListener.onTimerFinish();
    }

    private class DelayTimerTask implements Runnable {
        @Override public void run() {
            stop();
            onTimerFinish();
            if (BuildConfig.DEBUG) {
                Log.d("mbrc-log", "delay timer tick");
            }
        }
    }
    /**
     * This interface represents the TimerFinishEvent
     * The abstract method onTimerFinish() fires when the countdown finishes.
     */
    public interface TimerFinishEvent {
        public abstract void onTimerFinish();
    }
}
