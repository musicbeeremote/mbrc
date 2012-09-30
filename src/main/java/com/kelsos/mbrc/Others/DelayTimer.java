package com.kelsos.mbrc.Others;

import java.util.Timer;
import java.util.TimerTask;


public class DelayTimer
{

	private int _delay;
	private boolean _isRunning;
	private Timer _internalTimer;
	private InternalTimerTask _internalTimerTask;
	private TimerFinishEvent _timerFinishEventListener;

	/**
	 * This is the Default constructor of the DelayTimer class.
	 * The timer will fire an event after the specified period.
	 *
	 * @param delay The delay period in millisecond.
	 */
	public DelayTimer(int delay)
	{
		_delay = delay;
		_timerFinishEventListener = null;
	}

	public DelayTimer(int delay, TimerFinishEvent listener)
	{
		_delay = delay;
		_timerFinishEventListener = listener;
	}

	/**
	 * This method starts the delay timer's countdown.
	 */
	public void start()
	{
		if (_isRunning) return;
		if (_internalTimerTask != null)
		{
			_internalTimerTask.cancel();
			_internalTimerTask = null;
		}
		if (_internalTimer != null)
		{
			_internalTimer.cancel();
			_internalTimer.purge();
			_internalTimer = null;
		}
		_internalTimer = new Timer();
		_internalTimerTask = new InternalTimerTask();
		_internalTimer.schedule(_internalTimerTask, _delay);
		_isRunning = true;
	}

	/**
	 * This method stops the delay timer's countdown.
	 */
	public void stop()
	{
		if (_internalTimerTask != null)
		{
			_internalTimerTask.cancel();
			_internalTimerTask = null;
		}
		if (_internalTimer != null)
		{
			_internalTimer.cancel();
			_internalTimer.purge();
			_internalTimer = null;
		}
		_isRunning = false;
	}

	/**
	 * This method returns the status of the delay timer.
	 *
	 * @return true if the timer is running and false if not.
	 */
	public boolean isRunning()
	{
		return _isRunning;
	}

	/**
	 * Sets an event listener that listens for the timer finish event.
	 *
	 * @param listener The event listener
	 */
	public void setTimerFinishEventListener(TimerFinishEvent listener)
	{
		_timerFinishEventListener = listener;
	}

	private void onTimerFinish()
	{
		if (_timerFinishEventListener != null)
			_timerFinishEventListener.onTimerFinish();
	}

	/**
	 * This TimerTask when executed fires the onTimerFinishEvent and then stops the timer.
	 */
	private class InternalTimerTask extends TimerTask
	{

		public void run()
		{
			onTimerFinish();
			stop();
		}
	}

	/**
	 * This interface represents the TimerFinishEvent
	 * The abstract method onTimerFinish() fires when the countdown finishes.
	 */
	public interface TimerFinishEvent
	{
		public abstract void onTimerFinish();
	}
}
