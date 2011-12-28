package kelsos.mbremote.Network;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class AnswerHandler {
    private Context context;
    private DocumentBuilder db;
    private String _coverData;

    //Intents
    public final static String VOLUME_DATA = "kelsos.mbremote.action.VOLUME_DATA";
    public final static String PLAY_STATE = "kelsos.mbremote.action.PLAY_STATE";
    public final static String SONG_DATA = "kelsos.mbremote.action.SONG_DATA";
    public final static String SONG_COVER = "kelsos.mbremote.action.SONG_COVER";
    public final static String SONG_CHANGED = "kelsos.mbremote.action.SONG_CHANGED";
    public final static String MUTE_STATE = "kelsos.mbremote.action.MUTE_STATE";
    public final static String SCROBBLER_STATE = "kelsos.mbremote.action.SCROBBLER_STATE";
    public final static String REPEAT_STATE = "kelsos.mbremote.action.REPEAT_STATE";
    public final static String SHUFFLE_STATE = "kelsos.mbremote.action.SHUFFLE_STATE";
    
    public AnswerHandler() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getCoverData() {
        return _coverData;
    }

    public void clearCoverData() {
        _coverData = "";
    }

    public void answerProcessor(String answer) {
        try {
            String[] replies = answer.split("\0");
            for (String reply : replies) {
                Document doc = db.parse(new ByteArrayInputStream(reply.getBytes("UTF-8")));
                Node xmlNode = doc.getFirstChild();
                //Debug Options
                //Log.d("Node Name:", xmlNode.getNodeName());
                //Log.d("Node Value:", xmlNode.getTextContent());

                //AnswerIntent
                Intent notifyIntent = new Intent();

                if (xmlNode.getNodeName().contains("playPause")) {
                    Log.d("Reply Received", "<playPause>");
                } else if (xmlNode.getNodeName().contains("next")) {
                    Log.d("Reply Received", "<next>");
                } else if (xmlNode.getNodeName().contains("volume")) {
                    notifyIntent.setAction(VOLUME_DATA);
                    notifyIntent.putExtra("data", Integer.parseInt(xmlNode.getTextContent()));
                } else if (xmlNode.getNodeName().contains("songChanged")) {
                    if (xmlNode.getTextContent().contains("True")) {
                        notifyIntent.setAction(SONG_CHANGED);
                        //Log.d("SongChange","Cover Request to be send");
                    }
                } else if (xmlNode.getNodeName().contains("songInfo")) {
                    Node trackInfoNode = xmlNode.getFirstChild();
                    String artist = trackInfoNode.getTextContent();
                    trackInfoNode = trackInfoNode.getNextSibling();
                    String title = trackInfoNode.getTextContent();
                    trackInfoNode = trackInfoNode.getNextSibling();
                    String album = trackInfoNode.getTextContent();
                    trackInfoNode = trackInfoNode.getNextSibling();
                    String year = trackInfoNode.getTextContent();

                    notifyIntent.setAction(SONG_DATA);
                    notifyIntent.putExtra("artist", artist);
                    notifyIntent.putExtra("title", title);
                    notifyIntent.putExtra("album", album);
                    notifyIntent.putExtra("year", year);
                }
                else if (xmlNode.getNodeName().contains("songCover")) {
                    _coverData = xmlNode.getTextContent();
                    notifyIntent.setAction(SONG_COVER);
                    //Log.d("Cover:", "Cover - Received");
                }
                else if (xmlNode.getNodeName().contains("playState"))
                {
                	notifyIntent.setAction(PLAY_STATE);
                	notifyIntent.putExtra("playstate", xmlNode.getTextContent());
                }
                else if (xmlNode.getNodeName().contains("mute"))
                {
                	notifyIntent.setAction(MUTE_STATE);
                	notifyIntent.putExtra("state", xmlNode.getTextContent());
                }
                else if (xmlNode.getNodeName().contains("repeat"))
                {
                	notifyIntent.setAction(REPEAT_STATE);
                	notifyIntent.putExtra("state", xmlNode.getTextContent());
                }
                else if (xmlNode.getNodeName().contains("shuffle"))
                {
                	notifyIntent.setAction(SHUFFLE_STATE);
                	notifyIntent.putExtra("state", xmlNode.getTextContent());
                }
                else if (xmlNode.getNodeName().contains("scrobbler"))
                {
                	notifyIntent.setAction(SCROBBLER_STATE);
                	notifyIntent.putExtra("state", xmlNode.getTextContent());
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

}
