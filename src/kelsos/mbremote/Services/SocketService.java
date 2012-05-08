package kelsos.mbremote.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import kelsos.mbremote.Data.MusicTrack;
import kelsos.mbremote.Events.*;
import kelsos.mbremote.Messaging.*;
import kelsos.mbremote.Network.*;
import kelsos.mbremote.Others.Const;
import kelsos.mbremote.Others.DelayTimer;
import kelsos.mbremote.Others.SettingsManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * *****************
 */
public class SocketService {

    private static int _numberOfTries;
    public static final int MAX_RETRIES = 4;

    private Socket _cSocket;

    private PrintWriter _output;

    private Thread _connectionThread;

    private DelayTimer _connectionTimer;
    private DelayTimer _statusUpdateTimer;

    private SocketService()
    {
        _connectionTimer = new DelayTimer(1000);
        _statusUpdateTimer = new DelayTimer(2000);;

        _numberOfTries = 0; // Initialize the connection retry counter.

        _connectionTimer.setTimerFinishEventListener(timerFinishEvent);
        _statusUpdateTimer.setTimerFinishEventListener(statusUpdateTimerFinishEvent);

        _SocketDataEventSource = new SocketDataEventSource();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
            _nowPlayingList = new ArrayList<MusicTrack>();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        installFilter();
        _updateTimer = new DelayTimer(2000);
        // Event Listener for the communicator events
        _updateTimer.setTimerFinishEventListener(updateTimerFinishEvent);
    }

    DelayTimer.TimerFinishEvent statusUpdateTimerFinishEvent = new DelayTimer.TimerFinishEvent() {
        public void onTimerFinish() {
            _SocketDataEventSource.fireEvent(new SocketDataEvent(this,DataType.ConnectionState,String.valueOf(socketExistsAndIsConnected())));
        }
    };

    private DelayTimer.TimerFinishEvent timerFinishEvent = new DelayTimer.TimerFinishEvent() {
        public void onTimerFinish() {
            startSocketThread();
            _numberOfTries++;
        }
    };

    /**
     * This function starts the Thread that handles the socket connection.
     */
    private void startSocketThread() {
        if (socketExistsAndIsConnected() || connectionThreadExistsAndIsAlive())
            return;
        else if (!socketExistsAndIsConnected() && connectionThreadExistsAndIsAlive()) {
            _connectionThread.destroy();
            _connectionThread = null;
        }
        Runnable socketConnection = new socketConnection();
        _connectionThread = new Thread(socketConnection);
        _connectionThread.start();
        Log.d("ConnectivityHandler", "startSocketThread();");
    }

    private boolean connectionThreadExistsAndIsAlive() {
        return _connectionThread != null && _connectionThread.isAlive();
    }

    /**
     * Depending on the user input the function either retries to connect until the MAX_RETRIES number is reached
     * or it resets the number of retries counter and then retries to connect until the MAX_RETRIES number is reached
     *
     * @param input kelsos.mbremote.Network.Input.User resets the counter, kelsos.mbremote.Network.Input.System just tries one more time.
     */
    public void attemptToStartSocketThread(Input input) {
        if(!isOnline())
        {
            NotificationService.getInstance().showToastMessage("Check for Connection");
            return;
        }
        if (socketExistsAndIsConnected()) return;
        if (input == Input.user|| input == Input.initialize) {
            _numberOfTries = 0;
            if (_connectionTimer.isRunning()) _connectionTimer.stop();
        }
        if ((_numberOfTries > MAX_RETRIES) && _connectionTimer.isRunning()) {
            _connectionTimer.stop();
        } else if ((_numberOfTries < MAX_RETRIES) && !_connectionTimer.isRunning())
            _connectionTimer.start();
    }

    /**
     * Returns true if the socket is not null and it is connected, false in any other case.
     * @return Boolean
     */
    private boolean socketExistsAndIsConnected() {
        return _cSocket != null && _cSocket.isConnected();
    }

    protected void sendData(String data) {
        try {
            if (socketExistsAndIsConnected())
                _output.println(data + Const.NEWLINE);
        } catch (Exception ignored) {
        }
    }

    private class socketConnection implements Runnable {

        public void run() {
            Log.d("ConnectivityHandler", "connectSocket Running");
            SocketAddress socketAddress = SettingsManager.getInstance().getSocketAddress();
            if (null == socketAddress) return;
            BufferedReader _input;
            try {
                _cSocket = new Socket();
                _cSocket.connect(socketAddress);
                _output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_cSocket.getOutputStream())), true);
                _input = new BufferedReader(new InputStreamReader(_cSocket.getInputStream()));

                _SocketDataEventSource.fireEvent(new SocketDataEvent(this,DataType.ConnectionState,"true"));

                while (_cSocket.isConnected()) {
                    try {
                        final String serverAnswer = _input.readLine();
                        answerProcessor(serverAnswer);
                    } catch (IOException e) {
                        _input.close();
                        _cSocket.close();
                        throw e;
                    }
                }
            } catch (SocketTimeoutException e) {
                final String message = "Connection timed out";
                NotificationService.getInstance().showToastMessage(message);
            } catch (SocketException e) {
                final String exceptionMessage = e.toString().substring(26);
                NotificationService.getInstance().showToastMessage(exceptionMessage);
            } catch (IOException e) {
                Log.e("ConnectivityHandler", "Listening Loop", e);
            } catch (NullPointerException e) {
                Log.d("ConnectivityHandler", "NullPointer");
            } finally {
                if (_output != null) {
                    _output.flush();
                    _output.close();
                }
                _cSocket = null;

                _SocketDataEventSource.fireEvent(new SocketDataEvent(this, DataType.ConnectionState, "false"));

                Log.d("ConnectivityHandler", "ListeningThread terminated");
                attemptToStartSocketThread(Input.system);

            }
        }
    }
    private SocketDataEventSource _SocketDataEventSource;
    private static SocketService _instance;
    private DocumentBuilder db;
    private ArrayList<MusicTrack> _nowPlayingList;

    public static double ServerProtocolVersion;

    public ArrayList<MusicTrack> getNowPlayingList() {
        return _nowPlayingList;
    }

    public void clearNowPlayingList() {
        _nowPlayingList.clear();
    }

    public void addEventListener(SocketDataEventListener listener)
    {
        _SocketDataEventSource.addEventListener(listener);
    }

    public void removeEventListener(SocketDataEventListener listener)
    {
        _SocketDataEventSource.removeEventListener(listener);
    }


    /**
     * Returns the instance of the Singleton ReplyHandler if the instance exists,
     * or creates the instance and then it returns it when it does not exist.
     *
     * @return ReplyHandler Singleton Instance
     */
    public static synchronized SocketService getInstance() {
        if (_instance == null) {
            _instance = new SocketService();
        }
        return _instance;
    }

    /**
     * Given the socket server's answer this function processes the send data, extracts needed
     * information and then notifies the interested parts via Intents for the new changes/data.
     *
     * @param answer the answer that came from the server
     */
    public void answerProcessor(String answer) {
        try {
            String[] replies = answer.split("\0");
            for (String reply : replies) {
                Document doc = db.parse(new ByteArrayInputStream(reply.getBytes("UTF-8")));
                Node xmlNode = doc.getFirstChild();

                if (xmlNode.getNodeName().contains(Protocol.PLAYPAUSE)) {
                    Log.d("Reply Received", "<playPause>");
                } else if (xmlNode.getNodeName().contains(Protocol.NEXT)) {
                    Log.d("Reply Received", "<next>");
                } else if (xmlNode.getNodeName().contains(Protocol.VOLUME)) {
                    _SocketDataEventSource.fireEvent(new SocketDataEvent(this, DataType.Volume,xmlNode.getTextContent()));
                } else if (xmlNode.getNodeName().contains(Protocol.SONGCHANGED)) {
                    // DEPRECATED IN PROTOCOL 1.1
                } else if (xmlNode.getNodeName().contains(Protocol.SONGINFO)) {
                    getSongData(xmlNode);
                } else if (xmlNode.getNodeName().contains(Protocol.SONGCOVER)) {
                    _SocketDataEventSource.fireEvent(new SocketDataEvent(this, DataType.Bitmap, xmlNode.getTextContent()));
                } else if (xmlNode.getNodeName().contains(Protocol.PLAYSTATE)) {
                    _SocketDataEventSource.fireEvent(new SocketDataEvent(this, DataType.PlayState, xmlNode.getTextContent()));
                } else if (xmlNode.getNodeName().contains(Protocol.MUTE)) {
                    _SocketDataEventSource.fireEvent(new SocketDataEvent(this, DataType.MuteState, xmlNode.getTextContent()));
                } else if (xmlNode.getNodeName().contains(Protocol.REPEAT)) {
                    _SocketDataEventSource.fireEvent(new SocketDataEvent(this, DataType.RepeatState, xmlNode.getTextContent()));
                } else if (xmlNode.getNodeName().contains(Protocol.SHUFFLE)) {
                    _SocketDataEventSource.fireEvent(new SocketDataEvent(this,DataType.ShuffleState,xmlNode.getTextContent()));
                } else if (xmlNode.getNodeName().contains(Protocol.SCROBBLE)) {
                    _SocketDataEventSource.fireEvent(new SocketDataEvent(this, DataType.ScrobbleState, xmlNode.getTextContent()));
                } else if (xmlNode.getNodeName().contains(Protocol.PLAYLIST)) {
                    getPlaylistData(xmlNode);
                } else if (xmlNode.getNodeName().contains(Protocol.LYRICS)) {
                    //_songLyrics = xmlNode.getTextContent().replace("<p>", "\r\n").replace("<br>", "\n").replace("&lt;", "<").replace("&gt;", ">").replace("\"", "&quot;").replace("&apos;", "'").replace("&", "&amp;").replace("<p>", "\r\n").replace("<br>", "\n").trim();
                } else if (xmlNode.getNodeName().contains(Protocol.PLAYER_STATUS)) {
                    getPlayerStatus(xmlNode);
                } else if (xmlNode.getNodeName().contains(Protocol.PLAYER)){

                } else if(xmlNode.getNodeName().contains(Protocol.PROTOCOL)){
                    ServerProtocolVersion = Double.parseDouble(xmlNode.getTextContent());
                }
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Given a Node it extracts the Playlist data and then prepares the intent to be send.
     *
     * @param xmlNode
     *
     */
    private void getPlaylistData(Node xmlNode) {
        _nowPlayingList.clear();
        NodeList playlistData = xmlNode.getChildNodes();
        for (int i = 0; i < playlistData.getLength(); i++) {
            _nowPlayingList.add(new MusicTrack(playlistData.item(i).getFirstChild().getTextContent(), playlistData.item(i).getLastChild().getTextContent()));
        }
    }


    /**
     * When given a playerStatus node the function extracts the player status information and dispatched the related
     * events.
     * @param xmlNode
     */
    private void getPlayerStatus(Node xmlNode) {
        Node playerStatusNode = xmlNode.getFirstChild();
        _SocketDataEventSource.fireEvent(new SocketDataEvent(this,DataType.RepeatState,playerStatusNode.getTextContent()));
        playerStatusNode = playerStatusNode.getNextSibling();
        _SocketDataEventSource.fireEvent(new SocketDataEvent(this,DataType.MuteState,playerStatusNode.getTextContent()));
        playerStatusNode = playerStatusNode.getNextSibling();
        _SocketDataEventSource.fireEvent(new SocketDataEvent(this,DataType.ShuffleState,playerStatusNode.getTextContent()));
        playerStatusNode = playerStatusNode.getNextSibling();
        _SocketDataEventSource.fireEvent(new SocketDataEvent(this, DataType.ScrobbleState, playerStatusNode.getTextContent()));
        playerStatusNode = playerStatusNode.getNextSibling();
        _SocketDataEventSource.fireEvent(new SocketDataEvent(this, DataType.PlayState, playerStatusNode.getTextContent()));
        playerStatusNode = playerStatusNode.getNextSibling();
        _SocketDataEventSource.fireEvent(new SocketDataEvent(this, DataType.Volume, playerStatusNode.getTextContent()));
    }

    /**
     * This function gets an xml node containing the track information extracts the data and sends the respective events
     * to every on listening.
     *
     * @param xmlNode
     */
    private void getSongData(Node xmlNode) {
        Node trackInfoNode = xmlNode.getFirstChild();
        String[] trackData = new String[4];
        for (int i = 0; i < 4; i++) {
            trackData[i] = trackInfoNode.getTextContent();
            trackInfoNode = trackInfoNode.getNextSibling();
        }
        int index = 0;
        _SocketDataEventSource.fireEvent(new SocketDataEvent(this, DataType.Artist,trackData[index++]));
        _SocketDataEventSource.fireEvent(new SocketDataEvent(this, DataType.Title,trackData[index++]));
        _SocketDataEventSource.fireEvent(new SocketDataEvent(this, DataType.Album,trackData[index++]));
        _SocketDataEventSource.fireEvent(new SocketDataEvent(this, DataType.Year,trackData[index++]));
    }

    private DelayTimer _updateTimer;


    private DelayTimer.TimerFinishEvent updateTimerFinishEvent = new DelayTimer.TimerFinishEvent() {

        public void onTimerFinish() {
            requestAction(ProtocolHandler.PlayerAction.SongCover);
            requestAction(ProtocolHandler.PlayerAction.SongInformation);
            requestAction(ProtocolHandler.PlayerAction.PlayerStatus);
        }
    };

    public void requestPlayerData() {
        if(!_updateTimer.isRunning())
            _updateTimer.start();
    }

    public void requestAction(ProtocolHandler.PlayerAction action, String actionContent) {
        sendData(ProtocolHandler.getActionString(action, actionContent));
    }

    public void requestAction(ProtocolHandler.PlayerAction action) {
        sendData(ProtocolHandler.getActionString(action, ""));
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
                        requestAction(ProtocolHandler.PlayerAction.Volume, Integer.toString(newVolume));
                    }
                }
            }
            else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION))
            {
                NetworkInfo networkInfo = (NetworkInfo)intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if(networkInfo.getState().equals(NetworkInfo.State.CONNECTED))
                {
                   attemptToStartSocketThread(Input.user);
                }
                else if (networkInfo.getState().equals(NetworkInfo.State.DISCONNECTING))
                {

                }
            }
            else if(intent.getAction().equals(Const.CONNECTION_STATUS))
            {
                boolean status = intent.getBooleanExtra(Const.STATUS, false);
                if(status)
                {
                    requestAction(ProtocolHandler.PlayerAction.Player);
                    requestAction(ProtocolHandler.PlayerAction.Protocol);
                    requestPlayerData();
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
        connectivityHandler.getApplicationContext().registerReceiver(nmBroadcastReceiver, _nmFilter);
    }

    protected void finalize() {
        connectivityHandler.getApplicationContext().unregisterReceiver(nmBroadcastReceiver);
    }
}
