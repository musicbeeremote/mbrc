package com.kelsos.mbrc.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.Others.Const;
import com.kelsos.mbrc.Others.DelayTimer;
import com.kelsos.mbrc.Others.Protocol;
import com.kelsos.mbrc.data.MusicTrack;
import com.kelsos.mbrc.enums.ProtocolHandlerEventType;
import com.kelsos.mbrc.events.ProtocolDataEvent;
import com.squareup.otto.Bus;
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

import static android.os.Build.VERSION.SDK_INT;

@Singleton
public class ProtocolHandler
{
	private Bus bus;

	private boolean isHandshakeComplete;
	private DocumentBuilder db;

	public static double ServerProtocolVersion;

	@Inject
	public ProtocolHandler(Bus bus)
	{
		this.bus = bus;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try
		{
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		_updateTimer = new DelayTimer(2000, updateTimerFinishEvent);
	}

	public void setHandshakeComplete(boolean handshakeComplete)
	{
		isHandshakeComplete = handshakeComplete;
		if (isHandshakeComplete)
		{
			populateModel();
		}
	}

	/**
	 * Returns true if the handshake is complete and false if it is not.
	 * @return
	 */
	public boolean getHandshakeComplete()
	{
		return isHandshakeComplete;
	}

	/**
	 * Given the socket server's answer this function processes the send data, extracts needed
	 * information and then notifies the interested parts via Intents for the new changes/data.
	 *
	 * @param answer the answer that came from the server
	 */
	public void answerProcessor(String answer)
	{
		try
		{
			String[] replies = answer.split("\0");
			for (String reply : replies)
			{
				Document doc = db.parse(new ByteArrayInputStream(reply.getBytes("UTF-8")));
				Node xmlNode = doc.getFirstChild();

				if (xmlNode.getNodeName().contains(Protocol.NOT_ALLOWED))
				{
					bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_NOT_ALLOWED));
					return;
				}

				if (!isHandshakeComplete)
				{

					if (xmlNode.getNodeName().contains(Protocol.PLAYER))
					{
						requestAction(PlayerAction.Protocol);
					} else if (xmlNode.getNodeName().contains(Protocol.PROTOCOL))
					{
						ServerProtocolVersion = Double.parseDouble(xmlNode.getFirstChild().getNodeValue());
						isHandshakeComplete = true;
						bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_HANDSHAKE_COMPLETE, "true"));
						requestAction(PlayerAction.PlayerStatus);
						requestAction(PlayerAction.SongInformation);
						requestAction(PlayerAction.SongCover);
						requestAction(PlayerAction.Lyrics);
					} else
					{
						return;
					}
				}
				if (xmlNode.getNodeName().contains(Protocol.VOLUME))
				{
					bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_VOLUME_AVAILABLE, xmlNode.getFirstChild().getNodeValue()));
				} else if (xmlNode.getNodeName().contains(Protocol.SONGINFO))
				{
					getSongData(xmlNode);
					requestAction(PlayerAction.PlaybackPosition);
				} else if (xmlNode.getNodeName().contains(Protocol.SONGCOVER))
				{
					bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_COVER_AVAILABLE, xmlNode.getFirstChild().getNodeValue()));
				} else if (xmlNode.getNodeName().contains(Protocol.PLAYSTATE))
				{
					bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_PLAY_STATE_AVAILABLE, xmlNode.getFirstChild().getNodeValue()));
				} else if (xmlNode.getNodeName().contains(Protocol.MUTE))
				{
					bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_MUTE_STATE_AVAILABLE, xmlNode.getFirstChild().getNodeValue()));
				} else if (xmlNode.getNodeName().contains(Protocol.REPEAT))
				{
					bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_REPEAT_STATE_AVAILABLE, xmlNode.getFirstChild().getNodeValue()));
				} else if (xmlNode.getNodeName().contains(Protocol.SHUFFLE))
				{
					bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_SHUFFLE_STATE_AVAILABLE, xmlNode.getFirstChild().getNodeValue()));
				} else if (xmlNode.getNodeName().contains(Protocol.SCROBBLE))
				{
					bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_SCROBBLE_STATE_AVAILABLE, xmlNode.getFirstChild().getNodeValue()));
				} else if (xmlNode.getNodeName().contains(Protocol.PLAYLIST))
				{
					getPlaylistData(xmlNode);
				} else if (xmlNode.getNodeName().contains(Protocol.LYRICS))
				{
					String songLyrics ="";
					if (SDK_INT >= 8 && SDK_INT <= 10)
					{
						NodeList nodeList = xmlNode.getChildNodes();
						for(int i=0;i<nodeList.getLength();i++)
						{
							songLyrics+=nodeList.item(i).getNodeValue();
						}
					}
					else{
						songLyrics = xmlNode.getFirstChild().getNodeValue();
					}
					bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_LYRICS_AVAILABLE, songLyrics));
				} else if (xmlNode.getNodeName().contains(Protocol.PLAYER_STATUS))
				{
					getPlayerStatus(xmlNode);
				} else if (xmlNode.getNodeName().contains(Protocol.PLAYBACK_POSITION))
				{
					getTrackDurationInfo(xmlNode);
				}


			}
		} catch (SAXException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private void getPlaylistData(Node xmlNode)
	{
		ArrayList<MusicTrack> _nowPlayingList = new ArrayList<MusicTrack>();
		NodeList playlistData = xmlNode.getChildNodes();
		for (int i = 0; i < playlistData.getLength(); i++)
		{
			_nowPlayingList.add(new MusicTrack(playlistData.item(i).getFirstChild().getFirstChild().getNodeValue(), playlistData.item(i).getLastChild().getFirstChild().getNodeValue()));
		}
		bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_PLAYLIST_AVAILABLE, _nowPlayingList));
	}

	private void populateModel()
	{
		requestAction(ProtocolHandler.PlayerAction.Repeat, Const.STATE);
		requestAction(ProtocolHandler.PlayerAction.Shuffle, Const.STATE);
		requestAction(ProtocolHandler.PlayerAction.Scrobble, Const.STATE);
		requestAction(ProtocolHandler.PlayerAction.Mute, Const.STATE);
		requestAction(ProtocolHandler.PlayerAction.SongCover);
		requestAction(ProtocolHandler.PlayerAction.SongInformation);
		requestAction(ProtocolHandler.PlayerAction.Volume);
	}


	private void getTrackDurationInfo(Node xmNode)
	{
		String message;
		Node childNode = xmNode.getFirstChild();
		message = childNode.getFirstChild().getNodeValue() + "##";
		childNode = childNode.getNextSibling();
		message += childNode.getFirstChild().getNodeValue();
		bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_PLAYBACK_POSITION_AVAILABLE, message));

	}

	private void getPlayerStatus(Node xmlNode)
	{
		Node playerStatusNode = xmlNode.getFirstChild();
		bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_REPEAT_STATE_AVAILABLE, playerStatusNode.getFirstChild().getNodeValue()));
		playerStatusNode = playerStatusNode.getNextSibling();
		bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_MUTE_STATE_AVAILABLE, playerStatusNode.getFirstChild().getNodeValue()));
		playerStatusNode = playerStatusNode.getNextSibling();
		bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_SHUFFLE_STATE_AVAILABLE, playerStatusNode.getFirstChild().getNodeValue()));
		playerStatusNode = playerStatusNode.getNextSibling();
		bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_SCROBBLE_STATE_AVAILABLE, playerStatusNode.getFirstChild().getNodeValue()));
		playerStatusNode = playerStatusNode.getNextSibling();
		bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_PLAY_STATE_AVAILABLE, playerStatusNode.getFirstChild().getNodeValue()));
		playerStatusNode = playerStatusNode.getNextSibling();
		bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_VOLUME_AVAILABLE, playerStatusNode.getFirstChild().getNodeValue()));
	}

	private void getSongData(Node xmlNode)
	{
		NodeList trackInfoNodeList = xmlNode.getChildNodes();
		String[] trackData = new String[4];
		int nodeListLength = trackInfoNodeList.getLength();
		for (int i = 0; i < nodeListLength; i++)
		{
			trackData[i] = trackInfoNodeList.item(i).getFirstChild().getNodeValue();
		}

		int index = 0;
		bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_ARTIST_AVAILABLE, trackData[index++]));
		bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_TITLE_AVAILABLE, trackData[index++]));
		bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_ALBUM_AVAILABLE, trackData[index++]));
		bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_YEAR_AVAILABLE, trackData[index]));
	}

	private DelayTimer _updateTimer;


	private DelayTimer.TimerFinishEvent updateTimerFinishEvent = new DelayTimer.TimerFinishEvent()
	{

		public void onTimerFinish()
		{
			if (isHandshakeComplete)
			{
				requestAction(PlayerAction.SongCover);
				requestAction(PlayerAction.SongInformation);
				requestAction(PlayerAction.PlayerStatus);
				requestAction(PlayerAction.Lyrics);
			} else
			{
				requestAction(PlayerAction.Player);
			}
		}
	};

	public void requestPlayerData()
	{
		if (!_updateTimer.isRunning())
			_updateTimer.start();
	}

	public void requestAction(PlayerAction action, String actionContent)
	{
		if(!(action==PlayerAction.Protocol||action==PlayerAction.Player)&&!isHandshakeComplete) return;
		bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_REPLY_AVAILABLE, getActionString(action, actionContent)));
	}

	public void requestAction(ProtocolHandler.PlayerAction action)
	{
		if(!(action==PlayerAction.Protocol||action==PlayerAction.Player)&&!isHandshakeComplete) return;
		bus.post(new ProtocolDataEvent(ProtocolHandlerEventType.PROTOCOL_HANDLER_REPLY_AVAILABLE, getActionString(action, "")));
	}

	private static String PrepareXml(String name, String value)
	{
		return "<" + name + ">" + value + "</" + name + ">";
	}

	public enum PlayerAction
	{
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
		Player,
		PlaybackPosition
	}

	public static String getActionString(PlayerAction action, String value)
	{
		switch (action)
		{
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
				return PrepareXml(Protocol.PLAYER, value);
			case PlaybackPosition:
				return PrepareXml(Protocol.PLAYBACK_POSITION, value);
			default:
				return PrepareXml(Protocol.ERROR, "Invalid Request");
		}
	}

}
