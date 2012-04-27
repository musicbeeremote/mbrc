package kelsos.mbremote.Controller;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import kelsos.mbremote.Events.NewModelDataEvent;
import kelsos.mbremote.Events.NewModelDataEventListener;
import kelsos.mbremote.Network.ConnectivityHandler;
import kelsos.mbremote.Views.MainView;
import kelsos.mbremote.Models.MainDataModel;

import java.util.EventObject;

public class Controller extends Application {

    private static Controller _instance;
    Activity currentActivity;
    MainView mainView;

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
        startService(new Intent(activity, ConnectivityHandler.class));
       mainView = new MainView();
       launchActivity(mainView);
    }

    NewModelDataEventListener newModelDataEventListener = new NewModelDataEventListener() {
        @Override
        public void handleNewModelDataEvent(EventObject eventObject) {
            if(mainView==null) return;
            switch (((NewModelDataEvent) eventObject).getType()) {
                case Title:
                    mainView.updateTitleText(MainDataModel.getInstance().getTitle());
                    break;
                case Artist:
                    mainView.updateArtistText(MainDataModel.getInstance().getArtist());
                    break;
                case Album:
                    mainView.updateAlbumText(MainDataModel.getInstance().getAlbum());
                    break;
                case Year:
                    mainView.updateYearText(MainDataModel.getInstance().getYear());
                    break;
                case Volume:
                    mainView.updateVolumeData(MainDataModel.getInstance().getVolume());
                    break;
                case Bitmap:
                    mainView.updateAlbumCover(MainDataModel.getInstance().getAlbumCover());
                    break;
                case ConnectionState:
                    mainView.updateConnectionIndicator(MainDataModel.getInstance().getIsConnectionActive());
                    break;
                case RepeatState:
                    mainView.updateRepeatButtonState(MainDataModel.getInstance().getIsRepeatButtonActive());
                    break;
                case ShuffleState:
                    mainView.updateShuffleButtonState(MainDataModel.getInstance().getIsShuffleButtonActive());
                    break;
                case ScrobbleState:
                    mainView.updateScrobblerButtonState(MainDataModel.getInstance().getIsScrobbleButtonActive());
                    break;
                case MuteState:
                    mainView.updateMuteButtonState(MainDataModel.getInstance().getIsMuteButtonActive());
                    break;
                case PlayState:
                    mainView.updatePlayState(MainDataModel.getInstance().getPlayState());
                    break;
            }
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
