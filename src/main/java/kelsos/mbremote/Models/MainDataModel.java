package kelsos.mbremote.Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.squareup.otto.Bus;
import kelsos.mbremote.Async.ImageDecoder;
import kelsos.mbremote.Enumerations.PlayState;
import kelsos.mbremote.Enumerations.ProtocolHandlerEventType;
import kelsos.mbremote.Events.ModelDataEvent;
import kelsos.mbremote.Others.Const;
import kelsos.mbremote.R;
import roboguice.activity.event.OnCreateEvent;
import roboguice.event.Observes;
import roboguice.inject.InjectResource;

@Singleton
public class MainDataModel {

    private Bus bus;
	private Context context;

	@InjectResource(R.drawable.ic_image_no_cover) Drawable noCover;

	@Inject
    public MainDataModel(Bus bus, Context context){
		this.context = context;
		this.bus = bus;

       _title = _artist = _album = _year = "";
        _volume = 100;

        _isConnectionActive=false;
        _isRepeatButtonActive=false;
        _isShuffleButtonActive=false;
        _isScrobbleButtonActive=false;
        _isMuteButtonActive=false;
        _isDeviceOnline=false;
        _playState = PlayState.Stopped;

    }

	public void handleOnCreateEvent(@Observes OnCreateEvent e)
	{
		_albumCover = ((BitmapDrawable)noCover).getBitmap();
	}

    private String _title;
    private String _artist;
    private String _album;
    private String _year;
    private int _volume;
    private Bitmap _albumCover;

    private boolean _isConnectionActive;
    private boolean _isRepeatButtonActive;
    private boolean _isShuffleButtonActive;
    private boolean _isScrobbleButtonActive;
    private boolean _isMuteButtonActive;
    private boolean _isDeviceOnline;


    private PlayState _playState;

    public void setTitle(String title)
    {
        if(title.equals(_title)) return;
        _title=title;
        bus.post(new ModelDataEvent(ProtocolHandlerEventType.TITLE_AVAILABLE));
    }

    public String getTitle()
    {
        return _title;
    }

    public void setAlbum(String album)
    {
        if(album.equals(_album)) return;
        _album = album;
        bus.post(new ModelDataEvent(ProtocolHandlerEventType.Album));
    }

    public String getAlbum()
    {
        return _album;
    }

    public void setArtist(String artist)
    {
        if(artist.equals(_artist)) return;
        _artist = artist;
        bus.post(new ModelDataEvent(ProtocolHandlerEventType.ARTIST_AVAILABLE));
    }

    public String getArtist()
    {
        return _artist;
    }

    public void setYear(String year)
    {
        if(year.equals(_year)) return;
        _year=year;
        bus.post(new ModelDataEvent(ProtocolHandlerEventType.Year));
    }

    public String getYear()
    {
        return _year;
    }

    public void setVolume(String volume)
    {
        int newVolume = Integer.parseInt(volume);
        _volume = newVolume;
        bus.post(new ModelDataEvent(ProtocolHandlerEventType.Volume));
    }

    public int getVolume()
    {
        return _volume;
    }

    public void setAlbumCover(String base64format)
    {
        if(base64format==null || base64format== "")
        {
             setAlbumCover(((BitmapDrawable)noCover).getBitmap());
        }
        else
        {
            new ImageDecoder(context, base64format).execute();
        }
    }

    public void setAlbumCover(Bitmap cover)
    {
        _albumCover = cover;
        bus.post(new ModelDataEvent(ProtocolHandlerEventType.AlbumCover));
    }

    public Bitmap getAlbumCover()
    {
        return _albumCover;
    }

    public void setConnectionState(String connectionActive)
    {
        boolean newStatus = Boolean.parseBoolean(connectionActive);
        _isConnectionActive=newStatus;
        bus.post(new ModelDataEvent(ProtocolHandlerEventType.ConnectionState));
    }

    public boolean getIsConnectionActive()
    {
       return _isConnectionActive;
    }

    public void setRepeatState(String repeatButtonActive)
    {
        boolean newStatus = (repeatButtonActive.equals("All"));
        _isRepeatButtonActive = newStatus;
        bus.post(new ModelDataEvent(ProtocolHandlerEventType.RepeatState));
    }

    public boolean getIsRepeatButtonActive()
    {
        return _isRepeatButtonActive;
    }

    public void setShuffleState(String shuffleButtonActive)
    {
        boolean newStatus = Boolean.parseBoolean(shuffleButtonActive);
        _isShuffleButtonActive = newStatus;
        bus.post(new ModelDataEvent(ProtocolHandlerEventType.ShuffleState));
    }

    public boolean getIsShuffleButtonActive()
    {
        return _isShuffleButtonActive;
    }

    public void setScrobbleState(String scrobbleButtonActive)
    {
        boolean newStatus = Boolean.parseBoolean(scrobbleButtonActive);
        _isScrobbleButtonActive = newStatus;
        bus.post(new ModelDataEvent(ProtocolHandlerEventType.ScrobbleState));
    }

    public boolean getIsScrobbleButtonActive()
    {
        return _isScrobbleButtonActive;
    }

    public void setMuteState(String muteButtonActive)
    {
        boolean newStatus = Boolean.parseBoolean(muteButtonActive);
        _isMuteButtonActive = newStatus;
        bus.post(new ModelDataEvent(ProtocolHandlerEventType.MuteState));
    }

    public boolean getIsMuteButtonActive()
    {
        return _isMuteButtonActive;
    }

    public void setPlayState(String playState)
    {
        PlayState newState = PlayState.Undefined;
        if(playState.equalsIgnoreCase(Const.PLAYING)) newState = PlayState.Playing;
        else if (playState.equalsIgnoreCase(Const.STOPPED)) newState = PlayState.Stopped;
        else if (playState.equalsIgnoreCase(Const.PAUSED)) newState = PlayState.Paused;
        _playState = newState;
        bus.post(new ModelDataEvent(ProtocolHandlerEventType.PlayState));
    }

    public PlayState getPlayState()
    {
        return _playState;
    }

    public void setIsDeviceOnline(boolean value)
    {
        _isDeviceOnline = value;
        bus.post(new ModelDataEvent(ProtocolHandlerEventType.OnlineStatus));
    }

    public boolean getIsDeviceOnline()
    {
        return _isDeviceOnline;
    }

}

