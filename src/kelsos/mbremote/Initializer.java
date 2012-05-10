package kelsos.mbremote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import kelsos.mbremote.Controller.Controller;
import kelsos.mbremote.Others.DelayTimer;

public class Initializer extends Activity {
    private DelayTimer _passControlTimer;
    @Override
   public void onCreate(Bundle savedInstanceState)
   {
       super.onCreate(savedInstanceState);
       startService(new Intent(this,Controller.class));
       _passControlTimer = new DelayTimer(2000);
       _passControlTimer.setTimerFinishEventListener(passControl);

   }
   @Override
    public void onStart()
    {
        super.onStart();
        _passControlTimer.start();

    }

    DelayTimer.TimerFinishEvent passControl = new DelayTimer.TimerFinishEvent() {
        @Override
        public void onTimerFinish() {
            informController();
        }
    };

    public void informController()
    {
        Controller.getInstance().initialize(this);
    }

}
