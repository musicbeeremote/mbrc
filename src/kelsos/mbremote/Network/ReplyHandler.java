package kelsos.mbremote.Network;

import android.util.Log;
import kelsos.mbremote.Data.MusicTrack;
import kelsos.mbremote.Events.DataType;
import kelsos.mbremote.Events.SocketDataEvent;
import kelsos.mbremote.Events.SocketDataEventListener;
import kelsos.mbremote.Events.SocketDataEventSource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ReplyHandler {

    private SocketDataEventSource _SocketDataEventSource;
    private static ReplyHandler _instance;
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
     * The default constructor of the ReplyHandler Class.
     * it is set to private so than no instances of the singleton
     * can be created outside the class.
     */
    private ReplyHandler() {
        _SocketDataEventSource = new SocketDataEventSource();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
            _nowPlayingList = new ArrayList<MusicTrack>();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the instance of the Singleton ReplyHandler if the instance exists,
     * or creates the instance and then it returns it when it does not exist.
     *
     * @return ReplyHandler Singleton Instance
     */
    public static synchronized ReplyHandler getInstance() {
        if (_instance == null) {
            _instance = new ReplyHandler();
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
                    _SocketDataEventSource.fireEvent(new SocketDataEvent(this,DataType.Volume,xmlNode.getTextContent()));
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

}
