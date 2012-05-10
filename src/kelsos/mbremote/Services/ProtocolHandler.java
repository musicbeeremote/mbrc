package kelsos.mbremote.Services;

import android.util.Log;
import kelsos.mbremote.Events.DataType;
import kelsos.mbremote.Events.SocketDataEvent;
import kelsos.mbremote.Events.SocketDataEventListener;
import kelsos.mbremote.Events.SocketDataEventSource;
import kelsos.mbremote.Network.Protocol;
import kelsos.mbremote.Others.DelayTimer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ProtocolHandler {
    private SocketDataEventSource _SocketDataEventSource;

    private static ProtocolHandler _instance;

    private boolean isHandshakeComplete;
    private DocumentBuilder db;

    public static double ServerProtocolVersion;

    private ProtocolHandler()
    {
        _SocketDataEventSource = new SocketDataEventSource();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        _updateTimer = new DelayTimer(2000);
        // Event Listener for the communicator events
        _updateTimer.setTimerFinishEventListener(updateTimerFinishEvent);
    }

    public static synchronized ProtocolHandler getInstance()
    {
        if(_instance==null)
            _instance = new ProtocolHandler();
        return _instance;
    }

    public boolean isHandshakeComplete() {
        return isHandshakeComplete;
    }

    public void setHandshakeComplete(boolean handshakeComplete) {
        isHandshakeComplete = handshakeComplete;
    }

    public void addEventListener(SocketDataEventListener listener)
    {
        _SocketDataEventSource.addEventListener(listener);
    }

    public void removeEventListener(SocketDataEventListener listener)
    {
        _SocketDataEventSource.removeEventListener(listener);
    }

    public void updateConnectionStatus(String connectionStatus)
    {
        _SocketDataEventSource.dispatchEvent(new SocketDataEvent(this,DataType.ConnectionState,connectionStatus));
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
                    _SocketDataEventSource.dispatchEvent(new SocketDataEvent(this, DataType.Volume, xmlNode.getTextContent()));
                } else if (xmlNode.getNodeName().contains(Protocol.SONGCHANGED)) {
                    // DEPRECATED IN PROTOCOL 1.1
                } else if (xmlNode.getNodeName().contains(Protocol.SONGINFO)) {
                    getSongData(xmlNode);
                } else if (xmlNode.getNodeName().contains(Protocol.SONGCOVER)) {
                    _SocketDataEventSource.dispatchEvent(new SocketDataEvent(this, DataType.AlbumCover, xmlNode.getTextContent()));
                } else if (xmlNode.getNodeName().contains(Protocol.PLAYSTATE)) {
                    _SocketDataEventSource.dispatchEvent(new SocketDataEvent(this, DataType.PlayState, xmlNode.getTextContent()));
                } else if (xmlNode.getNodeName().contains(Protocol.MUTE)) {
                    _SocketDataEventSource.dispatchEvent(new SocketDataEvent(this, DataType.MuteState, xmlNode.getTextContent()));
                } else if (xmlNode.getNodeName().contains(Protocol.REPEAT)) {
                    _SocketDataEventSource.dispatchEvent(new SocketDataEvent(this, DataType.RepeatState, xmlNode.getTextContent()));
                } else if (xmlNode.getNodeName().contains(Protocol.SHUFFLE)) {
                    _SocketDataEventSource.dispatchEvent(new SocketDataEvent(this, DataType.ShuffleState, xmlNode.getTextContent()));
                } else if (xmlNode.getNodeName().contains(Protocol.SCROBBLE)) {
                    _SocketDataEventSource.dispatchEvent(new SocketDataEvent(this, DataType.ScrobbleState, xmlNode.getTextContent()));
                } else if (xmlNode.getNodeName().contains(Protocol.PLAYLIST)) {
                    getPlaylistData(xmlNode);
                } else if (xmlNode.getNodeName().contains(Protocol.LYRICS)) {
                    //_songLyrics = xmlNode.getTextContent().replace("<p>", "\r\n").replace("<br>", "\n").replace("&lt;", "<").replace("&gt;", ">").replace("\"", "&quot;").replace("&apos;", "'").replace("&", "&amp;").replace("<p>", "\r\n").replace("<br>", "\n").trim();
                } else if (xmlNode.getNodeName().contains(Protocol.PLAYER_STATUS)) {
                    getPlayerStatus(xmlNode);
                } else if (xmlNode.getNodeName().contains(Protocol.PLAYER))
                {
                    requestAction(PlayerAction.Protocol);
                } else if(xmlNode.getNodeName().contains(Protocol.PROTOCOL)){
                    ServerProtocolVersion = Double.parseDouble(xmlNode.getTextContent());
                    isHandshakeComplete=true;
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
        //_nowPlayingList.clear();
        NodeList playlistData = xmlNode.getChildNodes();
        for (int i = 0; i < playlistData.getLength(); i++) {
        //    _nowPlayingList.add(new MusicTrack(playlistData.item(i).getFirstChild().getTextContent(), playlistData.item(i).getLastChild().getTextContent()));
        }
    }


    /**
     * When given a playerStatus node the function extracts the player status information and dispatched the related
     * events.
     * @param xmlNode
     */
    private void getPlayerStatus(Node xmlNode) {
        Node playerStatusNode = xmlNode.getFirstChild();
        _SocketDataEventSource.dispatchEvent(new SocketDataEvent(this, DataType.RepeatState, playerStatusNode.getTextContent()));
        playerStatusNode = playerStatusNode.getNextSibling();
        _SocketDataEventSource.dispatchEvent(new SocketDataEvent(this, DataType.MuteState, playerStatusNode.getTextContent()));
        playerStatusNode = playerStatusNode.getNextSibling();
        _SocketDataEventSource.dispatchEvent(new SocketDataEvent(this, DataType.ShuffleState, playerStatusNode.getTextContent()));
        playerStatusNode = playerStatusNode.getNextSibling();
        _SocketDataEventSource.dispatchEvent(new SocketDataEvent(this, DataType.ScrobbleState, playerStatusNode.getTextContent()));
        playerStatusNode = playerStatusNode.getNextSibling();
        _SocketDataEventSource.dispatchEvent(new SocketDataEvent(this, DataType.PlayState, playerStatusNode.getTextContent()));
        playerStatusNode = playerStatusNode.getNextSibling();
        _SocketDataEventSource.dispatchEvent(new SocketDataEvent(this, DataType.Volume, playerStatusNode.getTextContent()));
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
        _SocketDataEventSource.dispatchEvent(new SocketDataEvent(this, DataType.Artist, trackData[index++]));
        _SocketDataEventSource.dispatchEvent(new SocketDataEvent(this, DataType.Title, trackData[index++]));
        _SocketDataEventSource.dispatchEvent(new SocketDataEvent(this, DataType.Album, trackData[index++]));
        _SocketDataEventSource.dispatchEvent(new SocketDataEvent(this, DataType.Year, trackData[index++]));
    }

    private DelayTimer _updateTimer;


    private DelayTimer.TimerFinishEvent updateTimerFinishEvent = new DelayTimer.TimerFinishEvent() {

        public void onTimerFinish() {
            if(isHandshakeComplete)
            {
                requestAction(PlayerAction.SongCover);
                requestAction(PlayerAction.SongInformation);
                requestAction(PlayerAction.PlayerStatus);
            }
            else
            {
                requestAction(PlayerAction.Player);
            }
        }
    };

    public void requestPlayerData() {
        if(!_updateTimer.isRunning())
            _updateTimer.start();
    }

    public void requestAction(PlayerAction action, String actionContent) {
        SocketService.getInstance().sendData(getActionString(action, actionContent));
    }

    public void requestAction(ProtocolHandler.PlayerAction action) {
        SocketService.getInstance().sendData(ProtocolHandler.getActionString(action, ""));
    }

    private static String PrepareXml(String name, String value) {
        return "<" + name + ">" + value + "</" + name + ">";
    }

    public enum PlayerAction {
        PlayPause,
        Previous,
        Next,
        Stop,
        PlayState,
        Volume,
        SongChangedStatus,
        SongInformation,
        SongCover,
        Shuffle,
        Mute,
        Repeat,
        Playlist,
        PlayNow,
        Scrobble,
        Lyrics,
        Rating,
        PlayerStatus,
        Protocol,
        Player
    }

    public static String getActionString(PlayerAction action, String value) {
        switch (action) {
            case PlayPause:
                return PrepareXml(Protocol.PLAYPAUSE, value);
            case Previous:
                return PrepareXml(Protocol.PREVIOUS, value);
            case Next:
                return PrepareXml(Protocol.NEXT, value);
            case Stop:
                return PrepareXml(Protocol.STOP, value);
            case PlayState:
                return PrepareXml(Protocol.PLAYSTATE, value);
            case Volume:
                return PrepareXml(Protocol.VOLUME, value);
            case SongChangedStatus:
                return PrepareXml(Protocol.SONGCHANGED, value);
            case SongInformation:
                return PrepareXml(Protocol.SONGINFO, value);
            case SongCover:
                return PrepareXml(Protocol.SONGCOVER, value);
            case Shuffle:
                return PrepareXml(Protocol.SHUFFLE, value);
            case Mute:
                return PrepareXml(Protocol.MUTE, value);
            case Repeat:
                return PrepareXml(Protocol.REPEAT, value);
            case Playlist:
                return PrepareXml(Protocol.PLAYLIST, value);
            case PlayNow:
                return PrepareXml(Protocol.PLAYNOW, value);
            case Scrobble:
                return PrepareXml(Protocol.SCROBBLE, value);
            case Lyrics:
                return PrepareXml(Protocol.LYRICS, value);
            case Rating:
                return PrepareXml(Protocol.RATING, value);
            case PlayerStatus:
                return PrepareXml(Protocol.PLAYER_STATUS, value);
            case Protocol:
                return PrepareXml(Protocol.PROTOCOL, value);
            case Player:
                return PrepareXml(Protocol.PLAYER,value);
            default:
                return PrepareXml(Protocol.ERROR, "Invalid Request");
        }
    }

}
