package kelsos.mbremote.Controller;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import kelsos.mbremote.Events.*;
import kelsos.mbremote.Models.MainDataModel;
import kelsos.mbremote.Network.Input;
import kelsos.mbremote.Network.ProtocolHandler;
import kelsos.mbremote.Others.SettingsManager;
import kelsos.mbremote.Services.SocketService;
import kelsos.mbremote.Views.MainView;

import java.util.EventObject;

public class Controller extends Service {

    private static Controller _instance;
    Activity currentActivity;

    @Override
    public void onCreate()
    {
        super.onCreate();
        _instance = this;
        MainDataModel.getInstance().addEventListener(modelDataEventListener);
        SocketService.getInstance().addEventListener(socketDataEventListener);
        SettingsManager.getInstance().setContext(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;  ||
    }

    @Override
    public void onDestroy()
    {
        unregisterReceiver(nmBroadcastReceiver);
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
        if(activity.getClass()==MainView.class)
        {
            ((MainView)activity).addEventListener(userActionEventListener);
        }

    }


    public void initialize(Activity activity)
    {
        SocketService.getInstance().attemptToStartSocketThread(Input.user);
        currentActivity = activity;
        launchMainActivity(activity);
    }

    UserActionEventListener userActionEventListener = new UserActionEventListener() {
        @Override
        public void handleUserActionEvent(EventObject eventObject) {
            switch (((UserActionEvent) eventObject).getUserAction()) {

                case PlayPause:
                    SocketService.getInstance().requestAction(ProtocolHandler.PlayerAction.PlayPause);
                    break;
                case Stop:
                    SocketService.getInstance().requestAction(ProtocolHandler.PlayerAction.Stop);
                    break;
                case Next:
                    SocketService.getInstance().requestAction(ProtocolHandler.PlayerAction.Next);
                    break;
                case Previous:
                    SocketService.getInstance().requestAction(ProtocolHandler.PlayerAction.Previous);
                    break;
                case Repeat:
                    SocketService.getInstance().requestAction(ProtocolHandler.PlayerAction.Repeat);
                    break;
                case Shuffle:
                    SocketService.getInstance().requestAction(ProtocolHandler.PlayerAction.Shuffle);
                    break;
                case Scrobble:
                    SocketService.getInstance().requestAction(ProtocolHandler.PlayerAction.Scrobble);
                    break;
                case Mute:
                    SocketService.getInstance().requestAction(ProtocolHandler.PlayerAction.Mute);
                    break;
                case Lyrics:
                    SocketService.getInstance().requestAction(ProtocolHandler.PlayerAction.Lyrics);
                    break;
                case Refresh:
                    SocketService.getInstance().requestPlayerData();
                    break;
                case Playlist:
                    SocketService.getInstance().requestAction(ProtocolHandler.PlayerAction.Playlist);
                    break;
                case Volume:
                    SocketService.getInstance().requestAction(ProtocolHandler.PlayerAction.Volume,((UserActionEvent) eventObject).getEventData());
                    break;
                case Initialize:
                    break;
            }
        }
    };

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
                case AlbumCover:
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
                case AlbumCover:
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

    private final BroadcastReceiver nmBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                Bundle bundle = intent.getExtras();
                if (null == bundle) return;
                String state = bundle.getString(TelephonyManager.EXTRA_STATE);
                if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
                    if (SettingsManager.getInstance().isVolumeReducedOnRinging()) {
                        int newVolume = ((int) (100 * 0.2));
                        SocketService.getInstance().requestAction(ProtocolHandler.PlayerAction.Volume, Integer.toString(newVolume));
                    }
                }
            }
            else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
            {
                NetworkInfo networkInfo = (NetworkInfo)intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if(networkInfo.getState().equals(NetworkInfo.State.CONNECTED))
                {
                    SocketService.getInstance().attemptToStartSocketThread(Input.user);
                }
                else if (networkInfo.getState().equals(NetworkInfo.State.DISCONNECTING))
                {

                }
            }
        }
    };

    /**
     * Initialized and installs the IntentFilter listening for the SONG_CHANGED
     * intent fired by the ReplyHandler or the PHONE_STATE intent fired by the
     * Android operating system.
     */
    private void installFilter() {
        IntentFilter _nmFilter = new IntentFilter();
        _nmFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        _nmFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(nmBroadcastReceiver, _nmFilter);
    }


    /**
     * Returns if the device is connected to internet/network
     * @return Boolean online status, true if online false if not.
     */
    private boolean isOnline()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo !=null && networkInfo.isConnected())
            return true;
        return false;
    }


}
