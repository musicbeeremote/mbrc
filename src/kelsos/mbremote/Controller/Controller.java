package kelsos.mbremote.Controller;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import kelsos.mbremote.Events.ModelDataEvent;
import kelsos.mbremote.Events.ModelDataEventListener;
import kelsos.mbremote.Events.SocketDataEvent;
import kelsos.mbremote.Events.SocketDataEventListener;
import kelsos.mbremote.Models.MainDataModel;
import kelsos.mbremote.Network.ConnectivityHandler;
import kelsos.mbremote.Network.ReplyHandler;
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
        MainDataModel.getInstance().addEventListener(modelDataEventListener);
        ReplyHandler.getInstance().addEventListener(socketDataEventListener);
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

    SocketDataEventListener socketDataEventListener = new SocketDataEventListener() {
        @Override
        public void handleSocketDataEvent(EventObject eventObject) {
            switch (((SocketDataEvent) eventObject).getType()) {

                case Title:
                    MainDataModel.getInstance().setTitle(((SocketDataEvent) eventObject).getData());
                    break;
                case Artist:
                    MainDataModel.getInstance().setArtist(((SocketDataEvent) eventObject).getData());
                    break;
                case Album:
                    MainDataModel.getInstance().setAlbum(((SocketDataEvent) eventObject).getData());
                    break;
                case Year:
                    MainDataModel.getInstance().setYear(((SocketDataEvent) eventObject).getData());
                    break;
                case Volume:
                    MainDataModel.getInstance().setVolume(((SocketDataEvent) eventObject).getData());
                    break;
                case Bitmap:
                    MainDataModel.getInstance().setAlbumCover(((SocketDataEvent) eventObject).getData());
                    break;
                case ConnectionState:
                    MainDataModel.getInstance().setConnectionState(((SocketDataEvent) eventObject).getData());
                    break;
                case RepeatState:
                    MainDataModel.getInstance().setRepeatState(((SocketDataEvent) eventObject).getData());
                    break;
                case ShuffleState:
                    MainDataModel.getInstance().setShuffleState(((SocketDataEvent) eventObject).getData());
                    break;
                case ScrobbleState:
                    MainDataModel.getInstance().setScrobbleState(((SocketDataEvent) eventObject).getData());
                    break;
                case MuteState:
                    MainDataModel.getInstance().setMuteState(((SocketDataEvent) eventObject).getData());
                    break;
                case PlayState:
                    MainDataModel.getInstance().setPlayState(((SocketDataEvent) eventObject).getData());
                    break;
            }
        }
    };

    ModelDataEventListener modelDataEventListener = new ModelDataEventListener() {
        @Override
        public void handleModelDataEvent(EventObject eventObject) {
            if(currentActivity.getClass()!=MainView.class) return;
            switch (((ModelDataEvent) eventObject).getType()) {
                case Title:
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainView)currentActivity).updateTitleText(MainDataModel.getInstance().getTitle());
                        }
                    });
                    break;
                case Artist:
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainView)currentActivity).updateArtistText(MainDataModel.getInstance().getArtist());
                        }
                    });
                    break;
                case Album:
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainView)currentActivity).updateAlbumText(MainDataModel.getInstance().getAlbum());
                        }
                    });
                    break;
                case Year:
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainView)currentActivity).updateYearText(MainDataModel.getInstance().getYear());
                        }
                    });
                    break;
                case Volume:
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainView)currentActivity).updateVolumeData(MainDataModel.getInstance().getVolume());
                        }
                    });
                    break;
                case Bitmap:
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainView)currentActivity).updateAlbumCover(MainDataModel.getInstance().getAlbumCover());
                        }
                    });
                    break;
                case ConnectionState:
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainView)currentActivity).updateConnectionIndicator(MainDataModel.getInstance().getIsConnectionActive());
                        }
                    });
                    break;
                case RepeatState:
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainView)currentActivity).updateRepeatButtonState(MainDataModel.getInstance().getIsRepeatButtonActive());
                        }
                    });
                    break;
                case ShuffleState:
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainView)currentActivity).updateShuffleButtonState(MainDataModel.getInstance().getIsShuffleButtonActive());
                        }
                    });
                    break;
                case ScrobbleState:
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainView)currentActivity).updateScrobblerButtonState(MainDataModel.getInstance().getIsScrobbleButtonActive());
                        }
                    });
                    break;
                case MuteState:
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainView)currentActivity).updateMuteButtonState(MainDataModel.getInstance().getIsMuteButtonActive());
                        }
                    });
                    break;
                case PlayState:
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainView)currentActivity).updatePlayState(MainDataModel.getInstance().getPlayState());
                        }
                    });
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
