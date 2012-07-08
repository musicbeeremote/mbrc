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
import android.util.Log;
import kelsos.mbremote.Data.MusicTrack;
import kelsos.mbremote.Events.*;
import kelsos.mbremote.Models.MainDataModel;
import kelsos.mbremote.Network.Input;
import kelsos.mbremote.Others.Const;
import kelsos.mbremote.Others.SettingsManager;
import kelsos.mbremote.Services.ProtocolHandler;
import kelsos.mbremote.Services.SocketService;
import kelsos.mbremote.Views.LyricsView;
import kelsos.mbremote.Views.MainView;
import kelsos.mbremote.Views.PlaylistView;

import java.util.ArrayList;
import java.util.EventObject;

public class Controller extends Service {

    private static Controller _instance;
    Activity currentActivity;

    private static boolean _isRunning = false;
    private SocketService _socketService;
    private ProtocolHandler _pHandler;

    private String _lyricsTemp;

    @Override
    public void onCreate()
    {
        super.onCreate();
        _instance = this;
        _socketService = new SocketService();
        _pHandler = new ProtocolHandler();
        MainDataModel.getInstance().addEventListener(modelDataEventListener);
        _pHandler.addEventListener(protocolDataEventListener);
        _socketService.addEventListener(rawSocketDataEventListener);
        SettingsManager.getInstance().setContext(this);
        _isRunning = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

    /**
     * Accessor to the running state of the Controller.
     * @return True is the Controller is already running.
     */
    public static boolean getIsRunning()
    {
        return _isRunning;
    }

    public void onActivityStart(Activity activity)
    {
        if(currentActivity.getClass()==MainView.class)
        {
            ((MainView)currentActivity).removeEventListener(userActionEventListener);
        }
        else if(currentActivity.getClass()==PlaylistView.class)
        {
            ((PlaylistView)currentActivity).removeEventListener(playlistViewListener);
        }

        if(activity.getClass()==MainView.class)
        {
            ((MainView)activity).addEventListener(userActionEventListener);
        }
        else if (activity.getClass()==PlaylistView.class)
        {
            ((PlaylistView)activity).addEventListener(playlistViewListener);
        }

        currentActivity = activity;
        if(activity.getClass()==MainView.class)
        {
            updateMainViewData();
        }
        else if (activity.getClass() == LyricsView.class)
        {
            currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((LyricsView)currentActivity).updateLyricsData(_lyricsTemp, MainDataModel.getInstance().getArtist(), MainDataModel.getInstance().getTitle());
                }
            });
        }
    }


    public void initialize(Activity activity)
    {
        _socketService.initSocketThread(Input.user);
        currentActivity = activity;

        if(activity.getClass()==MainView.class)
        {
            ((MainView)activity).addEventListener(userActionEventListener);
        }
    }

    UserActionEventListener userActionEventListener = new UserActionEventListener() {
        @Override
        public void handleUserActionEvent(EventObject eventObject) {
            Log.d("ACTION",((UserActionEvent) eventObject).getUserAction().toString());
            switch (((UserActionEvent) eventObject).getUserAction()) {

                case PlayPause:
                    _pHandler.requestAction(ProtocolHandler.PlayerAction.PlayPause);
                    break;
                case Stop:
                    _pHandler.requestAction(ProtocolHandler.PlayerAction.Stop);
                    break;
                case Next:
                    _pHandler.requestAction(ProtocolHandler.PlayerAction.Next);
                    break;
                case Previous:
                    _pHandler.requestAction(ProtocolHandler.PlayerAction.Previous);
                    break;
                case Repeat:
                    _pHandler.requestAction(ProtocolHandler.PlayerAction.Repeat, Const.TOGGLE);
                    break;
                case Shuffle:
                    _pHandler.requestAction(ProtocolHandler.PlayerAction.Shuffle, Const.TOGGLE);
                    break;
                case Scrobble:
                    _pHandler.requestAction(ProtocolHandler.PlayerAction.Scrobble, Const.TOGGLE);
                    break;
                case Mute:
                    _pHandler.requestAction(ProtocolHandler.PlayerAction.Mute, Const.TOGGLE);
                    break;
                case Lyrics:
                    _pHandler.requestAction(ProtocolHandler.PlayerAction.Lyrics);
                    break;
                case Refresh:
                    _pHandler.requestPlayerData();
                    break;
                case Playlist:
                    _pHandler.requestAction(ProtocolHandler.PlayerAction.Playlist);
                    break;
                case Volume:
                    _pHandler.requestAction(ProtocolHandler.PlayerAction.Volume,((UserActionEvent) eventObject).getEventData());
                    break;
                case PlaybackPosition:
                    _pHandler.requestAction(ProtocolHandler.PlayerAction.PlaybackPosition,((UserActionEvent) eventObject).getEventData());
                    break;
                case Initialize:
                    break;
            }
        }
    };

    ProtocolDataEventListener protocolDataEventListener = new ProtocolDataEventListener() {
        @Override
        public void handleSocketDataEvent(EventObject eventObject) {
            switch (((ProtocolDataEvent) eventObject).getType()) {

                case Title:
                    MainDataModel.getInstance().setTitle(((ProtocolDataEvent) eventObject).getData());
                    _pHandler.requestAction(ProtocolHandler.PlayerAction.PlaybackPosition,"status");
                    break;
                case Artist:
                    MainDataModel.getInstance().setArtist(((ProtocolDataEvent) eventObject).getData());
                    break;
                case Album:
                    MainDataModel.getInstance().setAlbum(((ProtocolDataEvent) eventObject).getData());
                    break;
                case Year:
                    MainDataModel.getInstance().setYear(((ProtocolDataEvent) eventObject).getData());
                    break;
                case Volume:
                    MainDataModel.getInstance().setVolume(((ProtocolDataEvent) eventObject).getData());
                    break;
                case AlbumCover:
                    if(((ProtocolDataEvent)eventObject).getData()!=null||((ProtocolDataEvent)eventObject).getData()!="")
                    {
                        MainDataModel.getInstance().setAlbumCover(((ProtocolDataEvent) eventObject).getData());
                    }
                    break;
                case ConnectionState:
                    MainDataModel.getInstance().setConnectionState(((ProtocolDataEvent) eventObject).getData());
                    break;
                case RepeatState:
                    MainDataModel.getInstance().setRepeatState(((ProtocolDataEvent) eventObject).getData());
                    break;
                case ShuffleState:
                    MainDataModel.getInstance().setShuffleState(((ProtocolDataEvent) eventObject).getData());
                    break;
                case ScrobbleState:
                    MainDataModel.getInstance().setScrobbleState(((ProtocolDataEvent) eventObject).getData());
                    break;
                case MuteState:
                    MainDataModel.getInstance().setMuteState(((ProtocolDataEvent) eventObject).getData());
                    break;
                case PlayState:
                    MainDataModel.getInstance().setPlayState(((ProtocolDataEvent) eventObject).getData());
                    break;
                case PlaybackPosition:
                    if(currentActivity.getClass()!=MainView.class) break;
                    String duration[] = ((ProtocolDataEvent) eventObject).getData().split("##");
                    final int current = Integer.parseInt(duration[0]);
                    final int total = Integer.parseInt(duration[1]);
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainView)currentActivity).updateDurationDisplay(current,total);
                        }
                    });
                    break;
                case Playlist:
                    if(currentActivity.getClass()!=PlaylistView.class) break;
                    final ArrayList<MusicTrack> nowPlaying = ((ProtocolDataEvent) eventObject).getTrackList();
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((PlaylistView) currentActivity).updateListData(nowPlaying);
                        }
                    });
                    break;
                case ReplyAvailable:
                    _socketService.sendData(((ProtocolDataEvent) eventObject).getData());
                    break;
                case Lyrics:
                    if(currentActivity.getClass()!= LyricsView.class)
                    {
                        Intent lvIntent = new Intent(currentActivity, LyricsView.class);
                        startActivity(lvIntent);
                        _lyricsTemp = ((ProtocolDataEvent) eventObject).getData();
                    }
                    else
                    {
                        final String lyrics = ((ProtocolDataEvent) eventObject).getData();
                        currentActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((LyricsView)currentActivity).updateLyricsData(lyrics, MainDataModel.getInstance().getArtist(), MainDataModel.getInstance().getTitle());
                            }
                        });

                    }
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

    PlaylistViewListener playlistViewListener = new PlaylistViewListener() {
        @Override
        public void handlePlaylistViewEvent(EventObject eventObject) {
            if(((PlaylistViewEvent) eventObject).getType()==PlaylistViewAction.GetPlaylist)
            {
                 _pHandler.requestAction(ProtocolHandler.PlayerAction.Playlist);
            }
            else if (((PlaylistViewEvent) eventObject).getType()==PlaylistViewAction.PlaySpecifiedTrack)
            {
                String track = ((PlaylistViewEvent) eventObject).getData();
                _pHandler.requestAction(ProtocolHandler.PlayerAction.PlayNow, track);
            }
        }
    };


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
                        _pHandler.requestAction(ProtocolHandler.PlayerAction.Volume, Integer.toString(newVolume));
                    }
                }
            }
            else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
            {
                NetworkInfo networkInfo = (NetworkInfo)intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if(networkInfo.getState().equals(NetworkInfo.State.CONNECTED))
                {
                    _socketService.initSocketThread(Input.user);
                }
                else if (networkInfo.getState().equals(NetworkInfo.State.DISCONNECTING))
                {

                }
            }
        }
    };

    /**
     * Used to update the data on the main view.
     */
    private void updateMainViewData()
    {
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((MainView)currentActivity).updateTitleText(MainDataModel.getInstance().getTitle());
                ((MainView)currentActivity).updateArtistText(MainDataModel.getInstance().getArtist());
                ((MainView)currentActivity).updateAlbumText(MainDataModel.getInstance().getAlbum());
                ((MainView)currentActivity).updateYearText(MainDataModel.getInstance().getYear());
                ((MainView)currentActivity).updateVolumeData(MainDataModel.getInstance().getVolume());
                ((MainView)currentActivity).updateAlbumCover(MainDataModel.getInstance().getAlbumCover());
                ((MainView)currentActivity).updateConnectionIndicator(MainDataModel.getInstance().getIsConnectionActive());
                ((MainView)currentActivity).updateRepeatButtonState(MainDataModel.getInstance().getIsRepeatButtonActive());
                ((MainView)currentActivity).updateShuffleButtonState(MainDataModel.getInstance().getIsShuffleButtonActive());
                ((MainView)currentActivity).updateScrobblerButtonState(MainDataModel.getInstance().getIsScrobbleButtonActive());
                ((MainView)currentActivity).updateMuteButtonState(MainDataModel.getInstance().getIsMuteButtonActive());
                ((MainView)currentActivity).updatePlayState(MainDataModel.getInstance().getPlayState());
            }
        });
    }

    RawSocketDataEventListener rawSocketDataEventListener = new RawSocketDataEventListener() {
        @Override
        public void handleRawSocketData(EventObject eventObject) {
            if(((RawSocketDataEvent)eventObject).getType()==RawSocketAction.PacketAvailable)
            {
                _pHandler.answerProcessor(((RawSocketDataEvent)eventObject).getData());
            }
            else if (((RawSocketDataEvent)eventObject).getType()==RawSocketAction.StatusChange)
            {
                MainDataModel.getInstance().setConnectionState(((RawSocketDataEvent)eventObject).getData());
                if(MainDataModel.getInstance().getIsConnectionActive())
                {
                    _pHandler.requestPlayerData();
                }
            }
            else if (((RawSocketDataEvent)eventObject).getType()==RawSocketAction.HandshakeUpdate)
            {
                _pHandler.setHandshakeComplete(Boolean.parseBoolean(((RawSocketDataEvent)eventObject).getData()));
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
