package kelsos.mbremote.Others;

import java.util.Timer;
import java.util.TimerTask;

public class DelayTimer {
    
    private int _delay;
    private boolean _isRunning;
    private Timer _internalTimer;
    private InternalTimerTask _internalTimerTask;
    private TimerFinishEvent _timerFinishEventListener;

    public DelayTimer(int delay)
    {
      _delay=delay;
        _timerFinishEventListener = null;
    }
    
    public void start()
    {
        if(_isRunning) return;
        if(_internalTimer==null) _internalTimer = new Timer();
        if(_internalTimerTask==null) _internalTimerTask = new InternalTimerTask();
        _internalTimer.schedule(_internalTimerTask,_delay);
    }

    public void stop()
    {
        _internalTimerTask.cancel();
        _internalTimerTask=null;
        _internalTimer.cancel();
        _internalTimer=null;
        _isRunning=false;
    }
    
    public void setTimerFinishEventListener(TimerFinishEvent listener)
    {
        _timerFinishEventListener = listener;
    }
    
    private void onTimerFinish()
    {
        if(_timerFinishEventListener!=null)
            _timerFinishEventListener.onTimerFinish();
    }

    private class InternalTimerTask extends TimerTask{

        @Override
        public void run() {
            onTimerFinish();
        }
    }

    public interface TimerFinishEvent
    {
        public abstract void onTimerFinish();
    }
}
