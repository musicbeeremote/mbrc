package kelsos.mbremote.Network;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;

import kelsos.mbremote.AppSettings;
import kelsos.mbremote.R;

public class NetworkManager extends Service {

    private Socket _cSocket = new Socket();
    private PrintWriter _output;
    private AnswerHandler _handler;
    private final IBinder _mBinder = new LocalBinder();
    
    private boolean _initialRun;

    private static final String STATE = "state";

    private static final String PLAYPAUSE = "<playPause/>";
    private static final String PREVIOUS = "<previous/>";
    private static final String NEXT = "<next/>";
    private static final String STOP = "<stopPlayback/>";
    private static final String PLAYSTATE = "<playState/>";
    private static final String VOLUME_OPEN = "<volume>";
    private static final String VOLUME_CLOSED = "</volume>";
    private static final String SONGCHANGED = "<songChanged/>";
    private static final String SONGINFO = "<songInfo/>";
    private static final String SONGCOVER = "<songCover/>";
    private static final String SHUFFLE_OPEN = "<shuffle>";
    private static final String SHUFFLE_CLOSE = "</shuffle>";
    private static final String MUTE_OPEN = "<mute>";
    private static final String MUTE_CLOSE = "</mute>";
    private static final String REPEAT_OPEN = "<repeat>";
    private static final String REPEAT_CLOSE = "</repeat>";
    private static final String PLAYLIST = "<playlist/>";
    private static final String PLAYNOW_OPEN = "<playNow>";
    private static final String PLAYNOW_CLOSE = "</playNow>";
    private static final String SCROBBLE_OPEN = "<scrobbler>";
    private static final String SCROBBLE_CLOSE = "</scrobbler>";

    public class LocalBinder extends Binder {
        public NetworkManager getService() {
            return NetworkManager.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return _mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _initialRun=true;
        _cSocket = new Socket();
        _handler = new AnswerHandler();
        _handler.setContext(getApplicationContext());
        Timer _pollingTimer = new Timer(true);
        PollingTimerTask _ptt = new PollingTimerTask();
        _pollingTimer.schedule(_ptt, 1000, 1000);
        IntentFilter _nmFilter = new IntentFilter();
        _nmFilter.addAction(AnswerHandler.SONG_CHANGED);
        _nmFilter.addAction("android.intent.action.PHONE_STATE");
        
        registerReceiver(nmBroadcastReceiver, _nmFilter);
    }

    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        startSocketThread();
    }
    
    private void startSocketThread()
    {
        Runnable connect = new connectSocket();
        new Thread(connect).start();
    }
    private void sendData(String sendData) {
        try {
        	if(_cSocket.isConnected())
        		_output.println(sendData + "\r\n");
        } catch (Exception e) {
            Log.e("SendData", "Failed", e);
        }
    }

    public void requestPlayPause() {
        this.sendData(PLAYPAUSE);
    }

    public void requestPreviousTrack() {
        this.sendData(PREVIOUS);
    }

    public void requestNextTrack() {
        this.sendData(NEXT);
    }

    public void requestStopPlayback() {
        this.sendData(STOP);
    }

    public void requestPlayState() {
        this.sendData(PLAYSTATE);
    }

    public void requestVolumeChange(int Volume) {
        this.sendData(VOLUME_OPEN + Volume + VOLUME_CLOSED);
    }

    public void requestSongChangedInformation() {
        this.sendData(SONGCHANGED);
    }

    public void requestCurrentlyPlayingSongInfo() {
        this.sendData(SONGINFO);
    }

    public void requestCurrentlyPlayingSongCover() {
        this.sendData(SONGCOVER);
    }

    public void requestShuffleState(String action) {
        this.sendData(SHUFFLE_OPEN + action + SHUFFLE_CLOSE);
    }

    public void requestMuteState(String action) {
        this.sendData(MUTE_OPEN + action + MUTE_CLOSE);
    }

    public void requestRepeatState(String action) {
        this.sendData(REPEAT_OPEN + action + REPEAT_CLOSE);
    }

    public void requestScrobblerState(String action) {
        this.sendData(SCROBBLE_OPEN + action + SCROBBLE_CLOSE);
    }

    public void requestNowPlayingList() {
        this.sendData(PLAYLIST);
    }


    public void requestPlaySelectedTrackNow(String selectedTrack) {
        this.sendData(PLAYNOW_OPEN + selectedTrack + PLAYNOW_CLOSE);
    }

    private class connectSocket implements Runnable {

        public void run() {
        	SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        	String server_hostname = sPrefs.getString(getApplicationContext().getString(R.string.settings_server_hostname), null);
        	int server_port = Integer.parseInt(sPrefs.getString(getApplicationContext().getString(R.string.settings_server_port),null));
        	Log.d("server_hostname",server_hostname + " " + server_port);
            SocketAddress socketAddress = new InetSocketAddress(server_hostname, server_port);
            try {
                _cSocket.connect(socketAddress);
                _output = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(_cSocket.getOutputStream())), true);
                BufferedReader _input = new BufferedReader(new InputStreamReader(
                        _cSocket.getInputStream()));
                while (_cSocket.isConnected()) {
                    try {
                        //Log.d("ServerInput", "next stop");
                        _handler.answerProcessor(_input.readLine());
                    } catch (IOException e) {
                        Log.e("MessageListening", "Failure", e);
                    }
                }
            } catch (Exception e) {
                Log.e("Socket Connection", "Failure", e);
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            _cSocket.close();
        } catch (IOException e) {
            Log.e("Socket Close", "Failure", e);
        }
        _cSocket = null;
        unregisterReceiver(nmBroadcastReceiver);
    }

    private class PollingTimerTask extends TimerTask {
        public void run() {
            requestUpdate();
        }
    }

    private void requestUpdate() {
    	if(_initialRun)
    	{
    		requestCurrentlyPlayingSongCover();
    		requestCurrentlyPlayingSongInfo();
    		_initialRun=false;
    	}
        requestSongChangedInformation();
        requestMuteState(STATE);
        requestRepeatState(STATE);
        requestScrobblerState(STATE);
        requestShuffleState(STATE);
        requestPlayState();
        requestVolumeChange(-1);
    }

    private final BroadcastReceiver nmBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AnswerHandler.SONG_CHANGED)) {
                requestCurrentlyPlayingSongCover();
            	requestCurrentlyPlayingSongInfo();
                //Log.d("Intent Received","Cover Requested");
            
            }
            if(intent.getAction().equals("android.intent.action.PHONE_STATE"))
            {
            	Bundle bundle = intent.getExtras();
            	if(null==bundle)
            		return;
            	String state = bundle.getString(TelephonyManager.EXTRA_STATE);
            	if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING))
            	{
            		SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                	if(sPrefs.getBoolean(getApplicationContext().getString(R.string.settings_reduce_volume_on_ring), false))
                		requestVolumeChange(20);
            	}
            }
        }
    };

    public String getCoverData() {
        return _handler.getCoverData();
    }

    public void clearCoverData() {
        _handler.clearCoverData();
    }
}
