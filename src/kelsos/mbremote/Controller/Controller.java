package kelsos.mbremote.Controller;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import kelsos.mbremote.Events.NewModelDataEventListener;
import kelsos.mbremote.MainActivity;
import kelsos.mbremote.Models.MainDataModel;

import java.util.EventObject;

public class Controller extends Application {

    private static Controller _instance;
    Activity currentActivity;
    MainActivity mainActivity;

    @Override
    public void onCreate()
    {
        _instance = this;
        MainDataModel.getInstance().addEventListener(newModelDataEventListener);
    }

    public static Controller getInstance()
    {
        return _instance;
    }

    public void initialize(Activity activity)
    {
        currentActivity = activity;
       mainActivity = new MainActivity();
       launchActivity(mainActivity);
    }

    NewModelDataEventListener newModelDataEventListener = new NewModelDataEventListener() {
        @Override
        public void handleNewModelDataEvent(EventObject eventObject) {
            ||
        }
    };

    private void launchActivity(Activity activity)
    {
        Intent launchIntent = new Intent(currentActivity,activity.getClass());
        currentActivity.startActivity(launchIntent);
        currentActivity.finish();
        //currentActivity = activity;

    }


}
