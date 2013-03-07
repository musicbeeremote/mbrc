package com.kelsos.mbrc.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.events.ModelEvent;
import com.kelsos.mbrc.events.MessageEvent;
import com.squareup.otto.Bus;
import com.kelsos.mbrc.others.Const;
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
        rating = 0;
	}

    private float rating;
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

    public void setRating(String rating){
        try {
           this.rating = Float.parseFloat(rating);
        } catch (Exception ex){
            this.rating = 0;
        }
        bus.post(new MessageEvent(ModelEvent.ModelRatingUpdated));
    }

    public float getRating(){
        return rating;
    }

	public void setTrackInfo(String artist, String album, String title, String year)
	{
		_artist = artist;
        _album = album;
        _year = year;
		_title = title;
		bus.post(new MessageEvent(ModelEvent.ModelTrackUpdated));
	}

	public String getTitle()
	{
		return _title;
	}

	public String getAlbum()
	{
		return _album;
	}

	public String getArtist()
	{
		return _artist;
	}

	public String getYear()
	{
		return _year;
	}

	public void setVolume(String volume)
	{
		_volume = Integer.parseInt(volume);
		bus.post(new MessageEvent(ModelEvent.ModelVolumeUpdated));
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
			bus.post(new MessageEvent(ModelEvent.ModelCoverNotFound));
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
		bus.post(new MessageEvent(ModelEvent.ModelCoverUpdated));
	}

	public Bitmap getAlbumCover()
	{
		return _albumCover;
	}

	public void setConnectionState(String connectionActive)
	{
		_isConnectionActive = Boolean.parseBoolean(connectionActive);
		bus.post(new MessageEvent(ModelEvent.ModelConnectionStateUpdated));
	}

	public boolean getIsConnectionActive()
	{
		return _isConnectionActive;
	}

	public void setRepeatState(String repeatButtonActive)
	{
		_isRepeatButtonActive = (repeatButtonActive.equals("All"));
		bus.post(new MessageEvent(ModelEvent.ModelRepeatStateUpdated));
	}

	public boolean getIsRepeatButtonActive()
	{
		return _isRepeatButtonActive;
	}

	public void setShuffleState(String shuffleButtonActive)
	{
		_isShuffleButtonActive = Boolean.parseBoolean(shuffleButtonActive);
		bus.post(new MessageEvent(ModelEvent.ModelShuffleStateUpdated));
	}

	public boolean getIsShuffleButtonActive()
	{
		return _isShuffleButtonActive;
	}

	public void setScrobbleState(String scrobbleButtonActive)
	{
		_isScrobbleButtonActive = Boolean.parseBoolean(scrobbleButtonActive);
		bus.post(new MessageEvent(ModelEvent.ModelScrobbleStateUpdated));
	}

	public boolean getIsScrobbleButtonActive()
	{
		return _isScrobbleButtonActive;
	}

	public void setMuteState(String muteButtonActive)
	{
		_isMuteButtonActive = Boolean.parseBoolean(muteButtonActive);
		bus.post(new MessageEvent(ModelEvent.ModelMuteStateUpdated));
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
		bus.post(new MessageEvent(ModelEvent.ModelPlayStateUpdated));
	}

	public PlayState getPlayState()
	{
		return _playState;
	}

	public void setLyrics(String lyrics)
	{
		if (lyrics.equals(_lyrics)) return;
		_lyrics = lyrics.replace("<p>", "\r\n").replace("<br>", "\n").replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&apos;", "'").replace("&amp;", "&").replace("<p>", "\r\n").replace("<br>", "\n").trim();
		bus.post(new MessageEvent(ModelEvent.ModelLyricsUpdated));
	}

	public String getLyrics()
	{
		return _lyrics;
	}

}

