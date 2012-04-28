package kelsos.mbremote;

import android.app.Activity;
import android.os.Bundle;
import kelsos.mbremote.Controller.Controller;

public class InitActivity extends Activity {
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
       super.onCreate(savedInstanceState);


   }
   @Override
    public void onStart()
    {
        super.onStart();
        Controller.getInstance().initialize(this);
    }
}
