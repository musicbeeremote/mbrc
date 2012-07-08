package kelsos.mbremote.Controller;

import android.app.Activity;
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
import com.google.inject.Inject;
import com.google.inject.Singleton;
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
import roboguice.event.Observes;
import roboguice.service.RoboService;

import java.util.ArrayList;

@Singleton
public class Controller extends RoboService {
    @Inject private SocketService socketService;
    @Inject private ProtocolHandler protocolHandler;
    @Inject private MainDataModel model;


    Activity currentActivity;

    private String _lyricsTemp;

    @Override
    public void onCreate()
    {
        super.onCreate();
        SettingsManager.getInstance().setContext(this);
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

    public void onActivityStart(Activity activity)
    {

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
                    ((LyricsView)currentActivity).updateLyricsData(_lyricsTemp, model.getArtist(), model.getTitle());
                }
            });
        }
    }


    public void handleUserActionEvent(@Observes UserActionEvent event) {

        switch (event.getUserAction()) {

            case PlayPause:
                protocolHandler.requestAction(ProtocolHandler.PlayerAction.PlayPause);
                break;
            case Stop:
                protocolHandler.requestAction(ProtocolHandler.PlayerAction.Stop);
                break;
            case Next:
                protocolHandler.requestAction(ProtocolHandler.PlayerAction.Next);
                break;
            case Previous:
                protocolHandler.requestAction(ProtocolHandler.PlayerAction.Previous);
                break;
            case Repeat:
                protocolHandler.requestAction(ProtocolHandler.PlayerAction.Repeat, Const.TOGGLE);
                break;
            case Shuffle:
                protocolHandler.requestAction(ProtocolHandler.PlayerAction.Shuffle, Const.TOGGLE);
                break;
            case Scrobble:
                protocolHandler.requestAction(ProtocolHandler.PlayerAction.Scrobble, Const.TOGGLE);
                break;
            case Mute:
                protocolHandler.requestAction(ProtocolHandler.PlayerAction.Mute, Const.TOGGLE);
                break;
            case Lyrics:
                protocolHandler.requestAction(ProtocolHandler.PlayerAction.Lyrics);
                break;
            case Refresh:
                protocolHandler.requestPlayerData();
                break;
            case Playlist:
                protocolHandler.requestAction(ProtocolHandler.PlayerAction.Playlist);
                break;
            case Volume:
                protocolHandler.requestAction(ProtocolHandler.PlayerAction.Volume, ((UserActionEvent) event).getEventData());
                break;
            case PlaybackPosition:
                protocolHandler.requestAction(ProtocolHandler.PlayerAction.PlaybackPosition, ((UserActionEvent) event).getEventData());
                break;
            case Initialize:
                break;
        }
    }

        public void handleSocketDataEvent(@Observes ProtocolDataEvent event) {
            switch (event.getType()) {

                case Title:
                    model.setTitle(event.getData());
                    protocolHandler.requestAction(ProtocolHandler.PlayerAction.PlaybackPosition, "status");
                    break;
                case Artist:
                    model.setArtist(event.getData());
                    break;
                case Album:
                    model.setAlbum(event.getData());
                    break;
                case Year:
                    model.setYear(event.getData());
                    break;
                case Volume:
                    model.setVolume(event.getData());
                    break;
                case AlbumCover:
                    if(event.getData()!=null|| event.getData()!="")
                    {
                        model.setAlbumCover(event.getData());
                    }
                    break;
                case ConnectionState:
                    model.setConnectionState(event.getData());
                    break;
                case RepeatState:
                    model.setRepeatState(event.getData());
                    break;
                case ShuffleState:
                    model.setShuffleState(event.getData());
                    break;
                case ScrobbleState:
                    model.setScrobbleState(event.getData());
                    break;
                case MuteState:
                    model.setMuteState(event.getData());
                    break;
                case PlayState:
                    model.setPlayState(event.getData());
                    break;
                case PlaybackPosition:
                    if(currentActivity.getClass()!=MainView.class) break;
                    String duration[] = event.getData().split("##");
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
                    final ArrayList<MusicTrack> nowPlaying = event.getTrackList();
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((PlaylistView) currentActivity).updateListData(nowPlaying);
                        }
                    });
                    break;
                case ReplyAvailable:
                    socketService.sendData(event.getData());
                    break;
                case Lyrics:
                    if(currentActivity.getClass()!= LyricsView.class)
                    {
                        Intent lvIntent = new Intent(currentActivity, LyricsView.class);
                        startActivity(lvIntent);
                        _lyricsTemp = event.getData();
                    }
                    else
                    {
                        final String lyrics = event.getData();
                        currentActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((LyricsView)currentActivity).updateLyricsData(lyrics, model.getArtist(), model.getTitle());
                            }
                        });

                    }
                    break;

            }
        }



        public void handleModelDataEvent(@Observes ModelDataEvent event) {
            if(currentActivity.getClass()!=MainView.class) return;
            switch (event.getType()) {
                case Title:
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainView)currentActivity).updateTitleText(model.getTitle());
                        }
                    });
                    break;
                case Artist:
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainView)currentActivity).updateArtistText(model.getArtist());
                        }
                    });
                    break;
                case Album:
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainView)currentActivity).updateAlbumText(model.getAlbum());
                        }
                    });
                    break;
                case Year:
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainView)currentActivity).updateYearText(model.getYear());
                        }
                    });
                    break;
                case Volume:
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainView)currentActivity).updateVolumeData(model.getVolume());
                        }
                    });
                    break;
                case AlbumCover:
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainView)currentActivity).updateAlbumCover(model.getAlbumCover());
                        }
                    });
                    break;
                case ConnectionState:
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainView)currentActivity).updateConnectionIndicator(model.getIsConnectionActive());
                        }
                    });
                    break;
                case RepeatState:
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainView)currentActivity).updateRepeatButtonState(model.getIsRepeatButtonActive());
                        }
                    });
                    break;
                case ShuffleState:
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainView)currentActivity).updateShuffleButtonState(model.getIsShuffleButtonActive());
                        }
                    });
                    break;
                case ScrobbleState:
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainView)currentActivity).updateScrobblerButtonState(model.getIsScrobbleButtonActive());
                        }
                    });
                    break;
                case MuteState:
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainView)currentActivity).updateMuteButtonState(model.getIsMuteButtonActive());
                        }
                    });
                    break;
                case PlayState:
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((MainView)currentActivity).updatePlayState(model.getPlayState());
                        }
                    });
                    break;
            }
        }


        public void handlePlaylistViewEvent(@Observes PlaylistViewEvent event) {
            if(event.getType()==PlaylistViewAction.GetPlaylist)
            {
                 protocolHandler.requestAction(ProtocolHandler.PlayerAction.Playlist);
            }
            else if (event.getType()==PlaylistViewAction.PlaySpecifiedTrack)
            {
                String track = event.getData();
                protocolHandler.requestAction(ProtocolHandler.PlayerAction.PlayNow, track);
            }
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
                        protocolHandler.requestAction(ProtocolHandler.PlayerAction.Volume, Integer.toString(newVolume));
                    }
                }
            }
            else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
            {
                NetworkInfo networkInfo = (NetworkInfo)intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if(networkInfo.getState().equals(NetworkInfo.State.CONNECTED))
                {
                    socketService.initSocketThread(Input.user);
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
                ((MainView)currentActivity).updateTitleText(model.getTitle());
                ((MainView)currentActivity).updateArtistText(model.getArtist());
                ((MainView)currentActivity).updateAlbumText(model.getAlbum());
                ((MainView)currentActivity).updateYearText(model.getYear());
                ((MainView)currentActivity).updateVolumeData(model.getVolume());
                ((MainView)currentActivity).updateAlbumCover(model.getAlbumCover());
                ((MainView)currentActivity).updateConnectionIndicator(model.getIsConnectionActive());
                ((MainView)currentActivity).updateRepeatButtonState(model.getIsRepeatButtonActive());
                ((MainView)currentActivity).updateShuffleButtonState(model.getIsShuffleButtonActive());
                ((MainView)currentActivity).updateScrobblerButtonState(model.getIsScrobbleButtonActive());
                ((MainView)currentActivity).updateMuteButtonState(model.getIsMuteButtonActive());
                ((MainView)currentActivity).updatePlayState(model.getPlayState());
            }
        });
    }

        public void handleRawSocketData(@Observes RawSocketDataEvent event) {
            if(event.getType()==RawSocketAction.PacketAvailable)
            {
                protocolHandler.answerProcessor(event.getData());
            }
            else if (event.getType()==RawSocketAction.StatusChange)
            {
                model.setConnectionState(event.getData());
                if(model.getIsConnectionActive())
                {
                    protocolHandler.requestPlayerData();
                }
            }
            else if (event.getType()==RawSocketAction.HandshakeUpdate)
            {
                protocolHandler.setHandshakeComplete(Boolean.parseBoolean(event.getData()));
            }

        }


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
