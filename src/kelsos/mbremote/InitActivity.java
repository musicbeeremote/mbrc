package kelsos.mbremote;

import android.app.Activity;
import android.os.Bundle;
import kelsos.mbremote.Controller.Controller;

public class InitActivity extends Activity {
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
       Controller.getInstance().initialize(this);
   }
}
