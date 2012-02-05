package kelsos.mbremote.Network;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

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

import kelsos.mbremote.Data.MusicTrack;

public class AnswerHandler {
	
	private static AnswerHandler _instance;
	private Context context;
	private DocumentBuilder db;
	private String _coverData;
	private ArrayList<MusicTrack> _nowPlayingList;
	private String _songLyrics;

	public ArrayList<MusicTrack> getNowPlayingList() {
		return _nowPlayingList;
	}
	
	public void  clearNowPlayingList()
	{
		_nowPlayingList.clear();
	}

	// Intents
	public final static String VOLUME_DATA = "kelsos.mbremote.action.VOLUME_DATA";
	public final static String PLAY_STATE = "kelsos.mbremote.action.PLAY_STATE";
	public final static String SONG_DATA = "kelsos.mbremote.action.SONG_DATA";
	public final static String SONG_COVER = "kelsos.mbremote.action.SONG_COVER";
	public final static String SONG_CHANGED = "kelsos.mbremote.action.SONG_CHANGED";
	public final static String MUTE_STATE = "kelsos.mbremote.action.MUTE_STATE";
	public final static String SCROBBLER_STATE = "kelsos.mbremote.action.SCROBBLER_STATE";
	public final static String REPEAT_STATE = "kelsos.mbremote.action.REPEAT_STATE";
	public final static String SHUFFLE_STATE = "kelsos.mbremote.action.SHUFFLE_STATE";
	public final static String PLAYLIST_DATA = "kelsos.mbremote.action.PLAYLIST_DATA";
	public final static String LYRICS_DATA = "kelsos.mbremote.action.LYRICS_DATA";

	/** The default constructor of the AnswerHandler Class. 
	 * it is set to private so than no instances of the singleton
	 * can be created outside the class.
	 */
	private AnswerHandler() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			db = dbf.newDocumentBuilder();
			_nowPlayingList = new ArrayList<MusicTrack>();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/** Returns the instance of the Singleton AnswerHandler if the instance exists,
	 * or creates the instance and then it returns it when it does not exist.
	 * @return
	 */
	public static synchronized AnswerHandler getInstance(){
		if(_instance==null)
		{
			_instance = new AnswerHandler();
		}
		return _instance;
	}

	/** Given the socket server's answer this function processes the send data, extracts needed
	 * information and then notifies the interested parts via Intents for the new changes/data.
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

				if (xmlNode.getNodeName().contains("playPause")) {
					Log.d("Reply Received", "<playPause>");
				} else if (xmlNode.getNodeName().contains("next")) {
					Log.d("Reply Received", "<next>");
				} else if (xmlNode.getNodeName().contains("volume")) {
					notifyIntent.setAction(VOLUME_DATA);
					notifyIntent.putExtra("data",
							Integer.parseInt(xmlNode.getTextContent()));
				} else if (xmlNode.getNodeName().contains("songChanged")) {
					if (xmlNode.getTextContent().contains("True")) {
						notifyIntent.setAction(SONG_CHANGED);
					}
				} else if (xmlNode.getNodeName().contains("songInfo")) {
					getSongData(xmlNode, notifyIntent);
				} else if (xmlNode.getNodeName().contains("songCover")) {
					_coverData = xmlNode.getTextContent();
					notifyIntent.setAction(SONG_COVER);
				} else if (xmlNode.getNodeName().contains("playState")) {
					notifyIntent.setAction(PLAY_STATE);
					notifyIntent
							.putExtra("playstate", xmlNode.getTextContent());
				} else if (xmlNode.getNodeName().contains("mute")) {
					notifyIntent.setAction(MUTE_STATE);
					notifyIntent.putExtra("state", xmlNode.getTextContent());
				} else if (xmlNode.getNodeName().contains("repeat")) {
					notifyIntent.setAction(REPEAT_STATE);
					notifyIntent.putExtra("state", xmlNode.getTextContent());
				} else if (xmlNode.getNodeName().contains("shuffle")) {
					notifyIntent.setAction(SHUFFLE_STATE);
					notifyIntent.putExtra("state", xmlNode.getTextContent());
				} else if (xmlNode.getNodeName().contains("scrobbler")) {
					notifyIntent.setAction(SCROBBLER_STATE);
					notifyIntent.putExtra("state", xmlNode.getTextContent());
				} else if (xmlNode.getNodeName().contains("playlist")) {
					getPlaylistData(xmlNode, notifyIntent);
				} else if (xmlNode.getNodeName().contains("lyrics"))
				{
					_songLyrics=xmlNode.getTextContent().replace("<p>", "\r\n").replace("<br>", "\n").replace("&lt;","<").replace("&gt;",">").replace("\"", "&quot;").replace("&apos;","'").replace("&", "&amp;").trim();
					notifyIntent.setAction(LYRICS_DATA);
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

	/** Given a Node it extracts the Playlist data and then prepares the intent to be send.
	 * @param xmlNode
	 * @param notifyIntent
	 */
	private void getPlaylistData(Node xmlNode, Intent notifyIntent) {
		_nowPlayingList.clear();
		NodeList playlistData = xmlNode.getChildNodes();
		for (int i=0; i<playlistData.getLength(); i++)
		{
			_nowPlayingList.add(new MusicTrack(playlistData.item(i).getFirstChild().getTextContent(),playlistData.item(i).getLastChild().getTextContent()));
		}
		notifyIntent.setAction(PLAYLIST_DATA);
	}

	/** Given a Node it extracts the song data and puts them inside an intent ready to be send.
	 * @param xmlNode
	 * @param notifyIntent
	 */
	private void getSongData(Node xmlNode, Intent notifyIntent) {
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
	
	/** This function returns the Song Lyrics String.
	 * @return
	 */
	public String getSongLyrics()
	{
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
