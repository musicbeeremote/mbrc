package com.kelsos.mbrc.others;

import java.util.Timer;
import java.util.TimerTask;


public class DelayTimer {
    private int delay;
    private boolean isRunning;
    private Timer mTimer;
    private InternalTimerTask mTimerTask;
    private TimerFinishEvent mTimerFinishListener;

    /**
     * This is the Default constructor of the DelayTimer class.
     * The timer will fire an event after the specified period.
     *
     * @param delay The delay period in millisecond.
     */
    public DelayTimer(int delay) {
        this.delay = delay;
        mTimerFinishListener = null;
    }

    public DelayTimer(int delay, TimerFinishEvent listener) {
        this.delay = delay;
        mTimerFinishListener = listener;
    }

    /**
     * This method starts the delay timer's countdown.
     */
    public void start() {
        if (isRunning) return;
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
        mTimer = new Timer(true);
        mTimerTask = new InternalTimerTask();
        mTimer.schedule(mTimerTask, delay);
        isRunning = true;
    }

    /**
     * This method stops the delay timer's countdown.
     */
    public void stop() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
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

    /**
     * This TimerTask when executed fires the onTimerFinishEvent and then stops the timer.
     */
    private class InternalTimerTask extends TimerTask {

        public void run() {
            onTimerFinish();
            stop();
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
