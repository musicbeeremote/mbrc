package kelsos.mbremote.Controller;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import kelsos.mbremote.Events.NewModelDataEvent;
import kelsos.mbremote.Events.NewModelDataEventListener;
import kelsos.mbremote.Models.MainDataModel;
import kelsos.mbremote.Network.ConnectivityHandler;
import kelsos.mbremote.Views.MainView;

import java.util.EventObject;

public class Controller extends Application {

    private static Controller _instance;
    Activity currentActivity;

    @Override
    public void onCreate()
    {
        super.onCreate();
        _instance = this;
        MainDataModel.getInstance().addEventListener(newModelDataEventListener);
    }

    public static Controller getInstance()
    {
        return _instance;
    }

    public void setCurrentActivity(Activity activity)
    {
        if(currentActivity!=null)
        {
            currentActivity.finish();
        }
        currentActivity = activity;
        currentActivity.startService(new Intent(currentActivity, ConnectivityHandler.class));
    }


    public void initialize(Activity activity)
    {
        currentActivity = activity;
        launchMainActivity(activity);
    }

    NewModelDataEventListener newModelDataEventListener = new NewModelDataEventListener() {
        @Override
        public void handleNewModelDataEvent(EventObject eventObject) {
            if(currentActivity.getClass()!=MainView.class) return;
            switch (((NewModelDataEvent) eventObject).getType()) {
                case Title:
                    ((MainView)currentActivity).updateTitleText(MainDataModel.getInstance().getTitle());
                    break;
                case Artist:
                    ((MainView)currentActivity).updateArtistText(MainDataModel.getInstance().getArtist());
                    break;
                case Album:
                    ((MainView)currentActivity).updateAlbumText(MainDataModel.getInstance().getAlbum());
                    break;
                case Year:
                    ((MainView)currentActivity).updateYearText(MainDataModel.getInstance().getYear());
                    break;
                case Volume:
                    ((MainView)currentActivity).updateVolumeData(MainDataModel.getInstance().getVolume());
                    break;
                case Bitmap:
                    ((MainView)currentActivity).updateAlbumCover(MainDataModel.getInstance().getAlbumCover());
                    break;
                case ConnectionState:
                    ((MainView)currentActivity).updateConnectionIndicator(MainDataModel.getInstance().getIsConnectionActive());
                    break;
                case RepeatState:
                    ((MainView)currentActivity).updateRepeatButtonState(MainDataModel.getInstance().getIsRepeatButtonActive());
                    break;
                case ShuffleState:
                    ((MainView)currentActivity).updateShuffleButtonState(MainDataModel.getInstance().getIsShuffleButtonActive());
                    break;
                case ScrobbleState:
                    ((MainView)currentActivity).updateScrobblerButtonState(MainDataModel.getInstance().getIsScrobbleButtonActive());
                    break;
                case MuteState:
                    ((MainView)currentActivity).updateMuteButtonState(MainDataModel.getInstance().getIsMuteButtonActive());
                    break;
                case PlayState:
                    ((MainView)currentActivity).updatePlayState(MainDataModel.getInstance().getPlayState());
                    break;
            }
        }
    };

    private void launchMainActivity(Activity activity)
    {
        if(activity.getClass() == MainView.class) return;
        Intent launchIntent;
        launchIntent = new Intent(activity, MainView.class);
        activity.startActivity(launchIntent);
    }


}
