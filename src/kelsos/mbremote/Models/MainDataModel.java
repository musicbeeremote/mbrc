package kelsos.mbremote.Models;

import android.app.backup.RestoreObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import kelsos.mbremote.Events.DataType;
import kelsos.mbremote.Events.NewModelDataEvent;
import kelsos.mbremote.Events.NewModelDataEventListener;
import kelsos.mbremote.Events.NewModelDataEventSource;
import kelsos.mbremote.Network.ReplyHandler;
import kelsos.mbremote.R;

public class MainDataModel {

    private static MainDataModel _instance;
    private NewModelDataEventSource _source;

    private MainDataModel(){
        _source = new NewModelDataEventSource();
    }

    public void addEventListener(NewModelDataEventListener listener)
    {
        _source.addEventListener(listener);
    }

    public void removeEventListener(NewModelDataEventListener listener)
    {
        _source.removeEventListener(listener);
    }

    public static synchronized MainDataModel getInstance()
    {
        if(_instance==null)
            _instance = new MainDataModel();
        return _instance;
    }

    private String _title;
    private String _artist;
    private String _album;
    private String _year;
    private int _volume;
    private Bitmap _albumCover;

    private Boolean _isConnectionActive;
    private Boolean _isRepeatButtonActivate;
    private Boolean _isShuffleButtonActivate;
    private Boolean _isScrobbleButtonActivate;
    private Boolean _isMuteButtonActive;
    private Boolean _isPlayActive;
    private Boolean _isStopActive;

    public void setTitle(String title)
    {
        if(_title.equals(title)) return;
        _title=title;
        _source.fireEvent(new NewModelDataEvent(this, DataType.Title));
    }

    public String getTitle()
    {
        return _title;
    }

    public void setAlbum(String album)
    {
        if(_album.equals(album)) return;
        _album = album;
        _source.fireEvent(new NewModelDataEvent(this,DataType.Album));
    }

    public String getAlbum()
    {
        return _album;
    }

    public void setArtist(String artist)
    {
        if(_artist.equals(artist)) return;
        _artist = artist;
        _source.fireEvent(new NewModelDataEvent(this,DataType.Artist));
    }

    public String getArtist()
    {
        return _album;
    }

    public void setYear(String year)
    {
        if(_year.equals(year)) return;
        _year=year;
        _source.fireEvent(new NewModelDataEvent(this,DataType.Year));
    }

    public void setVolume(String volume)
    {
        int newVolume = Integer.parseInt(volume);
        if(newVolume==_volume) return;
        _volume = newVolume;
        _source.fireEvent(new NewModelDataEvent(this, DataType.Volume));
    }

    public int getVolume()
    {
        return _volume;
    }

    public void setAlbumCover(String base64format)
    {
        new ImageDecodeTask().execute(base64format);
    }

    private void setAlbumCover(Bitmap cover)
    {
        _albumCover = cover;
        _source.fireEvent(new NewModelDataEvent(this,DataType.Bitmap));
    }

    public Bitmap getAlbumCover()
    {
        return _albumCover;
    }

    public void set_isConnectionActive

    private class ImageDecodeTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            byte[] decodedImage = Base64.decode(params[0] , Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            setAlbumCover(result);
        }
    }

}
