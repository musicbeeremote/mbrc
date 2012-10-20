package com.kelsos.mbrc.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.squareup.otto.Bus;
import com.kelsos.mbrc.events.ModelDataEvent;
import com.kelsos.mbrc.others.Const;
import com.kelsos.mbrc.enums.ModelDataEventType;
import com.kelsos.mbrc.enums.PlayState;
import com.kelsos.mbrc.utilities.ImageDecoder;

@Singleton
public class MainDataModel
{

	private Bus bus;
	private Context context;

	@Inject
	public MainDataModel(Bus bus, Context context)
	{
		this.context = context;
		this.bus = bus;

		_title = _artist = _album = _year = "";
		_volume = 100;

		_isConnectionActive = false;
		_isRepeatButtonActive = false;
		_isShuffleButtonActive = false;
		_isScrobbleButtonActive = false;
		_isMuteButtonActive = false;
		_playState = PlayState.Stopped;
		_albumCover = null;
	}

	private String _title;
	private String _artist;
	private String _album;
	private String _year;
	private String _lyrics;
	private int _volume;
	private Bitmap _albumCover;

	private boolean _isConnectionActive;
	private boolean _isRepeatButtonActive;
	private boolean _isShuffleButtonActive;
	private boolean _isScrobbleButtonActive;
	private boolean _isMuteButtonActive;


	private PlayState _playState;

	public void setTitle(String title)
	{
		if (title.equals(_title)) return;
		_title = title;
		bus.post(new ModelDataEvent(ModelDataEventType.MODEL_TITLE_UPDATED));
	}

	public String getTitle()
	{
		return _title;
	}

	public void setAlbum(String album)
	{
		if (album.equals(_album)) return;
		_album = album;
		bus.post(new ModelDataEvent(ModelDataEventType.MODEL_ALBUM_UPDATED));
	}

	public String getAlbum()
	{
		return _album;
	}

	public void setArtist(String artist)
	{
		if (artist.equals(_artist)) return;
		_artist = artist;
		bus.post(new ModelDataEvent(ModelDataEventType.MODEL_ARTIST_UPDATED));
	}

	public String getArtist()
	{
		return _artist;
	}

	public void setYear(String year)
	{
		if (year.equals(_year)) return;
		_year = year;
		bus.post(new ModelDataEvent(ModelDataEventType.MODEL_YEAR_UPDATED));
	}

	public String getYear()
	{
		return _year;
	}

	public void setVolume(String volume)
	{
		_volume = Integer.parseInt(volume);
		bus.post(new ModelDataEvent(ModelDataEventType.MODEL_VOLUME_UPDATED));
	}

	public int getVolume()
	{
		return _volume;
	}

	public void setAlbumCover(String base64format)
	{
		Log.d("Cover", base64format);
		if (base64format == null || base64format.equals(""))
		{
			bus.post(new ModelDataEvent(ModelDataEventType.MODEL_COVER_NOT_FOUND));
		} else
		{
			try {
				new ImageDecoder(context, base64format).execute();
			}
			catch (Exception ignore)
			{

			}
		}
	}

	public void setAlbumCover(Bitmap cover)
	{
		_albumCover = cover;
		bus.post(new ModelDataEvent(ModelDataEventType.MODEL_COVER_UPDATED));
	}

	public Bitmap getAlbumCover()
	{
		return _albumCover;
	}

	public void setConnectionState(String connectionActive)
	{
		_isConnectionActive = Boolean.parseBoolean(connectionActive);
		bus.post(new ModelDataEvent(ModelDataEventType.MODEL_CONNECTION_STATE_UPDATED));
	}

	public boolean getIsConnectionActive()
	{
		return _isConnectionActive;
	}

	public void setRepeatState(String repeatButtonActive)
	{
		_isRepeatButtonActive = (repeatButtonActive.equals("All"));
		bus.post(new ModelDataEvent(ModelDataEventType.MODEL_REPEAT_STATE_UPDATED));
	}

	public boolean getIsRepeatButtonActive()
	{
		return _isRepeatButtonActive;
	}

	public void setShuffleState(String shuffleButtonActive)
	{
		_isShuffleButtonActive = Boolean.parseBoolean(shuffleButtonActive);
		bus.post(new ModelDataEvent(ModelDataEventType.MODEL_SHUFFLE_STATE_UPDATED));
	}

	public boolean getIsShuffleButtonActive()
	{
		return _isShuffleButtonActive;
	}

	public void setScrobbleState(String scrobbleButtonActive)
	{
		_isScrobbleButtonActive = Boolean.parseBoolean(scrobbleButtonActive);
		bus.post(new ModelDataEvent(ModelDataEventType.MODEL_SCROBBLE_STATE_UPDATED));
	}

	public boolean getIsScrobbleButtonActive()
	{
		return _isScrobbleButtonActive;
	}

	public void setMuteState(String muteButtonActive)
	{
		_isMuteButtonActive = Boolean.parseBoolean(muteButtonActive);
		bus.post(new ModelDataEvent(ModelDataEventType.MODEL_MUTE_STATE_UPDATED));
	}

	public boolean getIsMuteButtonActive()
	{
		return _isMuteButtonActive;
	}

	public void setPlayState(String playState)
	{
		PlayState newState = PlayState.Undefined;
		if (playState.equalsIgnoreCase(Const.PLAYING)) newState = PlayState.Playing;
		else if (playState.equalsIgnoreCase(Const.STOPPED)) newState = PlayState.Stopped;
		else if (playState.equalsIgnoreCase(Const.PAUSED)) newState = PlayState.Paused;
		_playState = newState;
		bus.post(new ModelDataEvent(ModelDataEventType.MODEL_PLAY_STATE_UPDATED));
	}

	public PlayState getPlayState()
	{
		return _playState;
	}

	public void setLyrics(String lyrics)
	{
		if (lyrics.equals(_lyrics)) return;
		_lyrics = lyrics.replace("<p>", "\r\n").replace("<br>", "\n").replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&apos;", "'").replace("&amp;", "&").replace("<p>", "\r\n").replace("<br>", "\n").trim();
		bus.post(new ModelDataEvent(ModelDataEventType.MODEL_LYRICS_UPDATED));
	}

	public String getLyrics()
	{
		return _lyrics;
	}

}

