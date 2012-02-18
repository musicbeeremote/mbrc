package kelsos.mbremote.Network;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import kelsos.mbremote.Data.MusicTrack;
import kelsos.mbremote.Intents;
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

public class AnswerHandler {

    private static AnswerHandler _instance;
    private Context context;
    private DocumentBuilder db;
    private String _coverData;
    private ArrayList<MusicTrack> _nowPlayingList;
    private String _songLyrics;
    private int _currentVolume;

    public int getCurrentVolume() {
        return _currentVolume;
    }

    public ArrayList<MusicTrack> getNowPlayingList() {
        return _nowPlayingList;
    }

    public void clearNowPlayingList() {
        _nowPlayingList.clear();
    }

    /**
     * The default constructor of the AnswerHandler Class.
     * it is set to private so than no instances of the singleton
     * can be created outside the class.
     */
    private AnswerHandler() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        _currentVolume = 0;
        try {
            db = dbf.newDocumentBuilder();
            _nowPlayingList = new ArrayList<MusicTrack>();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the instance of the Singleton AnswerHandler if the instance exists,
     * or creates the instance and then it returns it when it does not exist.
     *
     * @return
     */
    public static synchronized AnswerHandler getInstance() {
        if (_instance == null) {
            _instance = new AnswerHandler();
        }
        return _instance;
    }

    /**
     * Given the socket server's answer this function processes the send data, extracts needed
     * information and then notifies the interested parts via Intents for the new changes/data.
     *
     * @param answer
     */
    public void answerProcessor(String answer) {
        try {
            String[] replies = answer.split("\0");
            for (String reply : replies) {
                Document doc = db.parse(new ByteArrayInputStream(reply
                        .getBytes("UTF-8")));
                Node xmlNode = doc.getFirstChild();
                Intent notifyIntent = new Intent();

                if (xmlNode.getNodeName().contains(Protocol.PLAYPAUSE)) {
                    Log.d("Reply Received", "<playPause>");
                } else if (xmlNode.getNodeName().contains(Protocol.NEXT)) {
                    Log.d("Reply Received", "<next>");
                } else if (xmlNode.getNodeName().contains(Protocol.VOLUME)) {
                    notifyIntent.setAction(Intents.VOLUME_DATA);
                    notifyIntent.putExtra(Protocol.DATA,
                            Integer.parseInt(xmlNode.getTextContent()));
                    _currentVolume = Integer.parseInt(xmlNode.getTextContent());
                } else if (xmlNode.getNodeName().contains(Protocol.SONGCHANGED)) {
                    if (xmlNode.getTextContent().contains("True")) {
                        notifyIntent.setAction(Intents.SONG_CHANGED);
                    }
                } else if (xmlNode.getNodeName().contains(Protocol.SONGINFO)) {
                    getSongData(xmlNode, notifyIntent);
                } else if (xmlNode.getNodeName().contains(Protocol.SONGCOVER)) {
                    _coverData = xmlNode.getTextContent();
                    notifyIntent.setAction(Intents.SONG_COVER);
                } else if (xmlNode.getNodeName().contains(Protocol.PLAYSTATE)) {
                    notifyIntent.setAction(Intents.PLAY_STATE);
                    notifyIntent
                            .putExtra(Protocol.STATE, xmlNode.getTextContent());
                } else if (xmlNode.getNodeName().contains(Protocol.MUTE)) {
                    notifyIntent.setAction(Intents.MUTE_STATE);
                    notifyIntent.putExtra(Protocol.STATE, xmlNode.getTextContent());
                } else if (xmlNode.getNodeName().contains(Protocol.REPEAT)) {
                    notifyIntent.setAction(Intents.REPEAT_STATE);
                    notifyIntent.putExtra(Protocol.STATE, xmlNode.getTextContent());
                } else if (xmlNode.getNodeName().contains(Protocol.SHUFFLE)) {
                    notifyIntent.setAction(Intents.SHUFFLE_STATE);
                    notifyIntent.putExtra(Protocol.STATE, xmlNode.getTextContent());
                } else if (xmlNode.getNodeName().contains(Protocol.SCROBBLE)) {
                    notifyIntent.setAction(Intents.SCROBBLER_STATE);
                    notifyIntent.putExtra(Protocol.STATE, xmlNode.getTextContent());
                } else if (xmlNode.getNodeName().contains(Protocol.PLAYLIST)) {
                    getPlaylistData(xmlNode, notifyIntent);
                } else if (xmlNode.getNodeName().contains(Protocol.LYRICS)) {
                    _songLyrics = xmlNode.getTextContent().replace("<p>", "\r\n").replace("<br>", "\n").replace("&lt;", "<").replace("&gt;", ">").replace("\"", "&quot;").replace("&apos;", "'").replace("&", "&amp;").replace("<p>", "\r\n").replace("<br>", "\n").trim();
                    notifyIntent.setAction(Intents.LYRICS_DATA);
                } else if(xmlNode.getNodeName().contains(Protocol.PLAYER_STATUS)){
                    getPlayerStatus(notifyIntent,xmlNode);
                }

                if (notifyIntent.getAction() != null)
                    context.sendBroadcast(notifyIntent);
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
     * @param notifyIntent
     */
    private void getPlaylistData(Node xmlNode, Intent notifyIntent) {
        _nowPlayingList.clear();
        NodeList playlistData = xmlNode.getChildNodes();
        for (int i = 0; i < playlistData.getLength(); i++) {
            _nowPlayingList.add(new MusicTrack(playlistData.item(i).getFirstChild().getTextContent(), playlistData.item(i).getLastChild().getTextContent()));
        }
        notifyIntent.setAction(Intents.PLAYLIST_DATA);
    }

    private void broadcastPlayerStateIntent(Intent intent, String action, String extras)
    {
        intent.setAction(action);
        intent.putExtra(Protocol.STATE,extras);
        context.sendBroadcast(intent);
    }

    private void getPlayerStatus(Intent intent, Node xmlNode) {
        Node playerStatusNode = xmlNode.getFirstChild();
        broadcastPlayerStateIntent(intent, Intents.REPEAT_STATE, playerStatusNode.getTextContent());
        playerStatusNode = playerStatusNode.getNextSibling();
        broadcastPlayerStateIntent(intent, Intents.MUTE_STATE, playerStatusNode.getTextContent());
        playerStatusNode = playerStatusNode.getNextSibling();
        broadcastPlayerStateIntent(intent, Intents.SHUFFLE_STATE, playerStatusNode.getTextContent());
        playerStatusNode = playerStatusNode.getNextSibling();
        broadcastPlayerStateIntent(intent, Intents.SCROBBLER_STATE, playerStatusNode.getTextContent());
        playerStatusNode = playerStatusNode.getNextSibling();
        broadcastPlayerStateIntent(intent, Intents.PLAY_STATE, playerStatusNode.getTextContent());
        playerStatusNode = playerStatusNode.getNextSibling();
        intent.setAction(Intents.VOLUME_DATA);
        intent.putExtra(Protocol.DATA, Integer.parseInt(playerStatusNode.getTextContent()));
    }

    /**
     * Given a Node it extracts the song data and puts them inside an intent ready to be send.
     *
     * @param xmlNode
     * @param notifyIntent
     */
    private void getSongData(Node xmlNode, Intent notifyIntent) {
        Node trackInfoNode = xmlNode.getFirstChild();
        String[] trackData = new String[4];
        for(int i = 0; i<4; i++)
        {
            trackData[i] = trackInfoNode.getTextContent();
            trackInfoNode = trackInfoNode.getNextSibling();
        }
        int index = 0;
        notifyIntent.setAction(Intents.SONG_DATA);
        notifyIntent.putExtra(Protocol.ARTIST, trackData[index++]);
        notifyIntent.putExtra(Protocol.TITLE, trackData[index++]);
        notifyIntent.putExtra(Protocol.ALBUM, trackData[index++]);
        notifyIntent.putExtra(Protocol.YEAR, trackData[index]);
    }

    /**
     * This function returns the Song Lyrics String.
     *
     * @return
     */
    public String getSongLyrics() {
        return _songLyrics;
    }

    public void clearLyrics() {
        _songLyrics = "";
    }

    public void clearCoverData() {
        _coverData = "";
    }

    public String getCoverData() {
        return _coverData;
    }

    public void setContext(Context context) {
        this.context = context;
    }

}
