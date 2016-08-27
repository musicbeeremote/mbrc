package com.kelsos.mbrc.helper;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.constants.Const;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.constants.ProtocolEventType;
import com.kelsos.mbrc.data.Queue;
import com.kelsos.mbrc.data.UserAction;
import com.kelsos.mbrc.data.library.Album;
import com.kelsos.mbrc.data.library.Artist;
import com.kelsos.mbrc.data.library.Genre;
import com.kelsos.mbrc.data.library.Track;
import com.kelsos.mbrc.events.MessageEvent;
import com.kelsos.mbrc.events.bus.RxBus;
import com.kelsos.mbrc.mappers.AlbumMapper;
import com.kelsos.mbrc.ui.activities.profile.AlbumTracksActivity;
import com.kelsos.mbrc.ui.activities.profile.ArtistAlbumsActivity;
import com.kelsos.mbrc.ui.activities.profile.GenreArtistsActivity;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PopupActionHandler {

  private RxBus bus;
  private BasicSettingsHelper settings;

  @Inject
  public PopupActionHandler(RxBus bus, BasicSettingsHelper settings) {
    this.bus = bus;
    this.settings = settings;
  }

  public void albumSelected(MenuItem menuItem, Album entry, Context context) {

    String query = entry.getAlbum();

    UserAction ua = null;
    switch (menuItem.getItemId()) {
      case R.id.popup_album_queue_next:
        ua = new UserAction(Protocol.LibraryQueueAlbum, new Queue(Queue.NEXT, query));
        break;
      case R.id.popup_album_queue_last:
        ua = new UserAction(Protocol.LibraryQueueAlbum, new Queue(Queue.LAST, query));
        break;
      case R.id.popup_album_play:
        ua = new UserAction(Protocol.LibraryQueueAlbum, new Queue(Queue.NOW, query));
        break;
      case R.id.popup_album_tracks:
        openProfile(entry, context);
        break;
      default:
        break;
    }

    if (ua != null) {
      bus.post(new MessageEvent(ProtocolEventType.UserAction, ua));
    }
  }

  public void artistSelected(MenuItem menuItem, Artist entry, Context context) {
    String query = entry.getArtist();

    UserAction ua = null;
    switch (menuItem.getItemId()) {
      case R.id.popup_artist_queue_next:
        ua = new UserAction(Protocol.LibraryQueueArtist, new Queue(Queue.NEXT, query));
        break;
      case R.id.popup_artist_queue_last:
        ua = new UserAction(Protocol.LibraryQueueArtist, new Queue(Queue.LAST, query));
        break;
      case R.id.popup_artist_play:
        ua = new UserAction(Protocol.LibraryQueueArtist, new Queue(Queue.NOW, query));
        break;
      case R.id.popup_artist_album:
        openProfile(entry, context);
        break;
      default:
        break;
    }

    if (ua != null) {
      bus.post(new MessageEvent(ProtocolEventType.UserAction, ua));
    }
  }

  public void genreSelected(MenuItem menuItem, Genre entry, Context context) {
    String query = entry.getGenre();

    UserAction ua = null;
    switch (menuItem.getItemId()) {
      case R.id.popup_genre_queue_next:
        ua = new UserAction(Protocol.LibraryQueueGenre, new Queue(Queue.NEXT, query));
        break;
      case R.id.popup_genre_queue_last:
        ua = new UserAction(Protocol.LibraryQueueGenre, new Queue(Queue.LAST, query));
        break;
      case R.id.popup_genre_play:
        ua = new UserAction(Protocol.LibraryQueueGenre, new Queue(Queue.NOW, query));
        break;
      case R.id.popup_genre_artists:
        openProfile(entry, context);
        break;
      default:
        break;
    }

    if (ua != null) {
      bus.post(new MessageEvent(ProtocolEventType.UserAction, ua));
    }
  }

  public void trackSelected(MenuItem menuItem, Track entry) {
    final String query = entry.getSrc();

    UserAction ua = null;
    switch (menuItem.getItemId()) {
      case R.id.popup_track_queue_next:
        ua = new UserAction(Protocol.LibraryQueueTrack, new Queue(Queue.NEXT, query));
        break;
      case R.id.popup_track_queue_last:
        ua = new UserAction(Protocol.LibraryQueueTrack, new Queue(Queue.LAST, query));
        break;
      case R.id.popup_track_play:
        ua = new UserAction(Protocol.LibraryQueueTrack, new Queue(Queue.NOW, query));
        break;
      default:
        break;
    }

    if (ua != null) {
      bus.post(new MessageEvent(ProtocolEventType.UserAction, ua));
    }
  }

  public void albumSelected(Album album, Context context) {
    String defaultAction = settings.getDefaultAction();
    if (!defaultAction.equals(Const.SUB)) {
      //noinspection WrongConstant
      Queue queue = new Queue(defaultAction, album.getAlbum());
      UserAction data = new UserAction(Protocol.LibraryQueueAlbum, queue);
      MessageEvent event = new MessageEvent(ProtocolEventType.UserAction, data);
      bus.post(event);
    } else {
      openProfile(album, context);
    }
  }

  public void artistSelected(Artist artist, Context context) {
    String defaultAction = settings.getDefaultAction();
    if (!defaultAction.equals(Const.SUB)) {
      //noinspection WrongConstant
      Queue queue = new Queue(defaultAction, artist.getArtist());
      UserAction data = new UserAction(Protocol.LibraryQueueArtist, queue);
      MessageEvent event = new MessageEvent(ProtocolEventType.UserAction, data);
      bus.post(event);
    } else {
      openProfile(artist, context);
    }
  }

  public void genreSelected(Genre genre, Context context) {
    String defaultAction = settings.getDefaultAction();
    if (!defaultAction.equals(Const.SUB)) {
      //noinspection WrongConstant
      Queue queue = new Queue(defaultAction, genre.getGenre());
      UserAction action = new UserAction(Protocol.LibraryQueueGenre, queue);
      MessageEvent event = new MessageEvent(ProtocolEventType.UserAction, action);
      bus.post(event);
    } else {
      openProfile(genre, context);
    }
  }

  public void trackSelected(Track track) {
    String defaultAction = settings.getDefaultAction();
    if (Const.SUB.equals(defaultAction)) {
      defaultAction = Queue.NOW;
    }
    //noinspection WrongConstant
    Queue queue = new Queue(defaultAction, track.getSrc());
    UserAction action = new UserAction(Protocol.LibraryQueueTrack, queue);
    MessageEvent event = new MessageEvent(ProtocolEventType.UserAction, action);
    bus.post(event);
  }

  private void openProfile(Artist artist, Context context) {
    Intent intent = new Intent(context, ArtistAlbumsActivity.class);
    intent.putExtra(ArtistAlbumsActivity.ARTIST_NAME, artist.getArtist());
    context.startActivity(intent);
  }

  private void openProfile(Album album, Context context) {
    AlbumMapper mapper = new AlbumMapper();
    Intent intent = new Intent(context, AlbumTracksActivity.class);
    intent.putExtra(AlbumTracksActivity.ALBUM, mapper.map(album));
    context.startActivity(intent);
  }

  private void openProfile(Genre genre, Context context) {
    Intent intent = new Intent(context, GenreArtistsActivity.class);
    intent.putExtra(GenreArtistsActivity.GENRE_NAME, genre.getGenre());
    context.startActivity(intent);
  }
}
